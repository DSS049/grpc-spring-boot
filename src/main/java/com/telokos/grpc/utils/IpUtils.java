package com.telokos.grpc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.UnknownHostException;

/**
 * This is a Utility class for getting the total bits of IP address
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpUtils {
    private static final int EXPECTED_OCTET_SIZE = 4;
    public static long ipV4ToLong(String ip) throws UnknownHostException {
        String[] octets = ip.split("\\.");
        int octetsSize= octets.length;
        long ipV4Value;
        if(octetsSize==EXPECTED_OCTET_SIZE) {
            ipV4Value= (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
                    + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
        }
        else{
            throw new UnknownHostException();
        }
        return ipV4Value;

    }
}
