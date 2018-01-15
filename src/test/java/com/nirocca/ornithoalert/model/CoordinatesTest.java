package com.nirocca.ornithoalert.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CoordinatesTest {

    @Test
    public void testShiftABit() throws Exception {
        String longitude = "12°48'24.13'' E";
        Coordinates coord = new Coordinates("51°58'58.13'' N", longitude);
        coord.shiftABit();
        coord.shiftABit();
        assertEquals(longitude, coord.getLongitude());
        assertEquals("51°59'08.13'' N", coord.getLatitude());
    }
    

}
