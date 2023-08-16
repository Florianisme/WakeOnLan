package de.florianisme.wakeonlan.ui.modify;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class BroadcastHelperTest {

    @Test
    public void testGetBroadcastAddress_withNoAvailableInterfaces() throws IOException {
        Enumeration<NetworkInterface> availableInterfaces = Collections.emptyEnumeration();

        TestBroadcastHelper broadcastHelper = new TestBroadcastHelper(availableInterfaces);
        Optional<InetAddress> broadcastAddress = broadcastHelper.getBroadcastAddress();

        assertFalse(broadcastAddress.isPresent());
    }

    @Test
    public void testGetBroadcastAddress_withSingleNonMatchingInterface() throws IOException {
        NetworkInterface singleInterface = mock(NetworkInterface.class);
        when(singleInterface.getName()).thenReturn("nonMatching");

        Enumeration<NetworkInterface> availableInterfaces = Collections.emptyEnumeration();

        TestBroadcastHelper broadcastHelper = new TestBroadcastHelper(availableInterfaces);
        Optional<InetAddress> broadcastAddress = broadcastHelper.getBroadcastAddress();

        assertFalse(broadcastAddress.isPresent());
    }

    @Test
    public void testGetBroadcastAddress_withSingleMatchingInterface() throws IOException {
        NetworkInterface singleInterface = mock(NetworkInterface.class);
        when(singleInterface.getName()).thenReturn("wlan0");

        InetAddress inetAddress = mock(InetAddress.class);
        InterfaceAddress interfaceAddress = mock(InterfaceAddress.class);
        when(interfaceAddress.getBroadcast()).thenReturn(inetAddress);

        List<InterfaceAddress> interfaceAddresses = Collections.singletonList(interfaceAddress);
        when(singleInterface.getInterfaceAddresses()).thenReturn(interfaceAddresses);

        Enumeration<NetworkInterface> availableInterfaces = Collections.enumeration(Collections.singletonList(singleInterface));

        TestBroadcastHelper broadcastHelper = new TestBroadcastHelper(availableInterfaces);
        Optional<InetAddress> broadcastAddress = broadcastHelper.getBroadcastAddress();

        assertTrue(broadcastAddress.isPresent());
        assertEquals(inetAddress, broadcastAddress.get());
    }

    private static class TestBroadcastHelper extends BroadcastHelper {

        private final Enumeration<NetworkInterface> networkInterfaces;

        private TestBroadcastHelper(Enumeration<NetworkInterface> networkInterfaces) {
            this.networkInterfaces = networkInterfaces;
        }

        @Override
        protected Enumeration<NetworkInterface> getNetworkInterfaces() {
            return networkInterfaces;
        }
    }

}