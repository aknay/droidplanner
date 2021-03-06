package org.droidplanner.android.proxy.mission.item.markers;

import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.drone.mission.item.complex.Survey;

import org.droidplanner.android.maps.MarkerInfo;

/**
 */
public class PolygonMarkerInfo extends MarkerInfo.SimpleMarkerInfo {

	private LatLong mPoint;
    private final Survey survey;
    private final int polygonIndex;

	public PolygonMarkerInfo(LatLong point, Survey mSurvey, int index) {
		mPoint = point;
		survey = mSurvey;
		polygonIndex = index;
	}
	
	public Survey getSurvey(){
		return survey;
	}

	public int getIndex(){
		return polygonIndex;
	}

	
	@Override
	public float getAnchorU() {
		return 0.5f;
	}

	@Override
	public float getAnchorV() {
		return 0.5f;
	}

	@Override
	public com.o3dr.services.android.lib.coordinate.LatLong getPosition() {
		return mPoint;
	}

	@Override
	public void setPosition(LatLong coord) {
		mPoint = coord;
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isFlat() {
		return true;
	}
}
