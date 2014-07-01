package edu.fordham.cis.mobileauc.gatt;

import java.util.UUID;

/**
 * Holds the UUIDs for each Attribute the GATT Servers/Clients will need
 * Created on 7/1/14.
 *
 * @author Andrew Johnston
 * @version 0.01
 */
public class MobileAucGattAttributes {

    public UUID UUID_BID          = UUID.fromString("c5665cf2-60ac-4d74-91bf-02cb0226d089");
    public UUID UUID_ASK          = UUID.fromString("cf4c2539-e77a-4e20-9f23-581dc2a38fcf");
    public UUID UUID_SEND_DATA    = UUID.fromString("12dd676a-f307-4027-93cd-6b485f079585");
    public UUID UUID_RECEIVE_DATA = UUID.fromString("265eb23c-e480-47ed-a4e9-6a81519869f7");
}
