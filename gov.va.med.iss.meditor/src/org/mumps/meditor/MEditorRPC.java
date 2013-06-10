package org.mumps.meditor;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.FoundationsException;

public class MEditorRPC {

	private VistaLinkConnection connection;

	public MEditorRPC(VistaLinkConnection connection) {
		this.connection = connection;
	}

	public String getRoutineFromServer(String routineName) throws RoutineNotFoundException {

		try {
			RpcRequest vReq = RpcRequestFactory.getRpcRequest("",
					"XT ECLIPSE M EDITOR");
			vReq.setUseProprietaryMessageFormat(false);
			vReq.getParams().setParam(1, "string", "RL"); // RD RL GD GL RS
			vReq.getParams().setParam(2, "string", "notused");
			vReq.getParams().setParam(3, "string", routineName);
			RpcResponse vResp = connection.executeRPC(vReq);
			String result = vResp.getResults();
			if (result.equals("-1^Error Processing load request")) {
				throw new RoutineNotFoundException();
			} else {
				return result.substring(result.indexOf('\n')+1);
			}
		} catch (FoundationsException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
