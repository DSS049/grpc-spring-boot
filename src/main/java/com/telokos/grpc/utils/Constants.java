package com.telokos.grpc.utils;

public class Constants {

    //Region
    public static final int REGION_ASIA = 1;
    public static final int REGION_NORTH = 2 ;
    public static final int REGION_WEST = 3 ;

    public static final int WORKER_ID = 65535;

    //MachineID Bit Allocation
    public static final int NODE_ID_BITS =16;
    //MAX BIT
    public static final int MAX_NODE_ID_BITS = 57;
    public static final int BASE_CONVERSION = 36;
    //Max MachineId
    public static final int MAX_MACHINE_ID= (int)(Math.pow(2, Constants.NODE_ID_BITS) - 1);

    // Custom Epoch (January 1, 2010 Midnight UTC = 2010-01-01T00:00:00Z)
    public static final long CUSTOM_EPOCH = 1262284200000L ;


}
