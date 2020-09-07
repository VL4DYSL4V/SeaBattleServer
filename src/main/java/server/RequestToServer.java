package server;

import enums.RequestType;

import java.io.Serializable;

public class RequestToServer implements Serializable {

    private static final long serialVersionUID = -2451197194385146848L;

    private String fromWhom;
    private RequestType requestType;

}
