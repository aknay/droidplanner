package org.droidplanner.core.drone.profiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.droidplanner.core.MAVLink.MavLinkParameters;
import org.droidplanner.core.drone.Drone;
import org.droidplanner.core.drone.DroneInterfaces;
import org.droidplanner.core.drone.DroneInterfaces.DroneEventsType;
import org.droidplanner.core.drone.DroneVariable;
import org.droidplanner.core.parameters.Parameter;

import android.util.Log;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.ardupilotmega.msg_param_value;

/**
 * Class to manage the communication of parameters to the MAV.
 * 
 * Should be initialized with a MAVLink Object, so the manager can send messages
 * via the MAV link. The function processMessage must be called with every new
 * MAV Message.
 * 
 */
public class Parameters extends DroneVariable {


	private HashMap<Integer,Parameter> parameters = new HashMap<Integer,Parameter>();
	
	public DroneInterfaces.OnParameterManagerListener parameterListener;

	public Parameters(Drone myDrone) {
		super(myDrone);
	}

	public void getAllParameters() {
		parameters.clear();
		if (parameterListener != null)
			parameterListener.onBeginReceivingParameters();
		MavLinkParameters.requestParametersList(myDrone);
	}

	/**
	 * Try to process a Mavlink message if it is a parameter related message
	 * 
	 * @param msg
	 *            Mavlink message to process
	 * @return Returns true if the message has been processed
	 */
	public boolean processMessage(MAVLinkMessage msg) {
		if (msg.msgid == msg_param_value.MAVLINK_MSG_ID_PARAM_VALUE) {
			processReceivedParam((msg_param_value) msg);
			return true;
		}
		return false;
	}

	private void processReceivedParam(msg_param_value m_value) {
		// collect params in parameter list
		Parameter param = new Parameter(m_value);
		parameters.put((int) m_value.param_index,param);

		// update listener
		if (parameterListener != null)
			parameterListener.onParameterReceived(param, m_value.param_index, m_value.param_count);
			
		// Are all parameters here? Notify the listener with the parameters
		if (parameters.size()>= m_value.param_count) {
			if (parameterListener != null) {
				List<Parameter> parameterList = new ArrayList<Parameter>();
				for(int key : parameters.keySet()) {
					parameterList.add(parameters.get(key));
				}
				Log.d("param","all params are here");
				parameterListener.onEndReceivingParameters(parameterList);
			}
		//if the final parameter arrived but the hashmap is incomplete, go thru the whole hashmap and request every single missing value
		}else if(m_value.param_index==m_value.param_count-1){
			reRequestMissingParams(m_value);		
			Log.d("param","final param arrived, but something's missing");
		}
		
		Log.d("param",m_value.param_index+ " out of " + m_value.param_count);
		if(m_value.param_index==m_value.param_count-1)
			Log.d("param","final param arrived");
		
		myDrone.events.notifyDroneEvent(DroneEventsType.PARAMETER);
	}

	private void reRequestMissingParams(msg_param_value m_value) {
		for (int i=0;i<m_value.param_count;i++){
			if(!parameters.containsKey(i)){
				MavLinkParameters.readParameter(myDrone, i);
			}
		}
	}

	public void sendParameter(Parameter parameter) {
		MavLinkParameters.sendParameter(myDrone, parameter);
	}

	public void ReadParameter(String name) {
		MavLinkParameters.readParameter(myDrone, name);
	}

	public Parameter getParameter(String name) {
		for (int key : parameters.keySet()) {
			if (parameters.get(key).name.equalsIgnoreCase(name))
				return parameters.get(key);
		}
		return null;
	}

	public Parameter getLastParameter() {
		if (parameters.size() > 0)
			return parameters.get(parameters.size() - 1);

		return null;
	}
}
