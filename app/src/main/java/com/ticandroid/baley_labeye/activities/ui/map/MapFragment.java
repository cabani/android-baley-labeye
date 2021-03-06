package com.ticandroid.baley_labeye.activities.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ticandroid.baley_labeye.BuildConfig;
import com.ticandroid.baley_labeye.R;
import com.ticandroid.baley_labeye.beans.MuseumBean;
import com.ticandroid.baley_labeye.utils.Caster;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Map Fragment used to display all the museums stored in the FS instance on a
 * osm map.
 *
 * @author Labeye
 * @see Fragment
 */
public class MapFragment extends Fragment {

    /**
     * Current map view being displayed.
     **/
    private transient MapView mMapView;
    /**
     * Our current firestore instance.
     */
    private transient final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    /**
     * The list of museum ids in visit collection.
     */
    private transient List<String> listOfMuseumIdInVisits;

    /**
     * Draw a marker with the given geopoint.
     *
     * @param geoPoint geopoint where the marker needs to be drawn
     */
    private void drawMarker(GeoPoint geoPoint, String museumName, int numberOfVisits) {
        Marker marker = new Marker(mMapView);
        marker.setPosition(geoPoint);
        marker.setTitle(museumName);
        marker.setSnippet(String.format("Nombre de visites : %s", numberOfVisits));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlayManager().add(marker);
        Log.d(getClass().getName(), String.format("marker added to gp : %s", geoPoint.toString()));
    }

    /**
     * Default constructor.
     */
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Creates an instance of the fragment.
     *
     * @return a new map fragment
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        super.onCreate(savedInstanceState);
        // Create map view
        mMapView = root.findViewById(R.id.map_museum_view);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        // Focus on the geographical center of the metropolitan france
        final MapController mMapController = (MapController) mMapView.getController();
        final GeoPoint centerOfFrance = new GeoPoint(47.1539d, 0.22508d);
        mMapController.setCenter(centerOfFrance);
        mMapController.setZoom(7);
        // Add geomarkers for museums
        CollectionReference visites = firebaseFirestore.collection("visites");
        // We only need to know the number of visits for each museum, so we can only save its id per visit and count it later
        listOfMuseumIdInVisits = new ArrayList<>();
        visites.get().addOnCompleteListener(k -> k.getResult().forEach(element -> listOfMuseumIdInVisits.add(element.getString("idMusee"))));
        CollectionReference collectionReference = firebaseFirestore.collection("museums");
        collectionReference.get().addOnCompleteListener(k ->
                k.getResult().forEach(element -> {
                            try {
                                MuseumBean museum = new MuseumBean();
                                museum.setNomDuMusee(element.getString("nomDuMusee"));
                                museum.setCoordonneesFinales(element.getString("coordonneesFinales"));
                                final int numberOfVisits = (int) listOfMuseumIdInVisits.stream().filter(visitId -> visitId.equals(element.getId())).count();
                                final double[] position = Caster.positionToDoubleArray(
                                        Objects.requireNonNull(museum.getCoordonneesFinales(),
                                                String.format("Position is null for %s", element.getId()))
                                );
                                drawMarker(new GeoPoint(position[0], position[1]), museum.getNomDuMusee(), numberOfVisits);
                            } catch (Exception e) {
                                Log.e(getClass().getName(), e.getMessage());
                            }
                        }
                )
        );
        return root;
    }
}