package io.ipfs.api;

import io.ipfs.multiaddr.*;
import org.junit.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class MultiAddressTest {

    @Test
    public void fails() {
        List<String> parsed = Stream.of(
                "/ip4",
                "/ip4/::1",
                "/ip4/fdpsofodsajfdoisa",
                "/ip6",
                "/udp",
                "/tcp",
                "/sctp",
                "/udp/65536",
                "/tcp/65536",
                "/onion/9imaq4ygg2iegci7:80",
                "/onion/aaimaq4ygg2iegci7:80",
                "/onion/timaq4ygg2iegci7:0",
                "/onion/timaq4ygg2iegci7:-1",
                "/onion/timaq4ygg2iegci7",
                "/onion/timaq4ygg2iegci@:666",
                "/udp/1234/sctp",
                "/udp/1234/udt/1234",
                "/udp/1234/utp/1234",
                "/ip4/127.0.0.1/udp/jfodsajfidosajfoidsa",
                "/ip4/127.0.0.1/udp",
                "/ip4/127.0.0.1/tcp/jfodsajfidosajfoidsa",
                "/ip4/127.0.0.1/tcp",
                "/ip4/127.0.0.1/ipfs",
                "/ip4/127.0.0.1/ipfs/tcp",
                "/quic/65536",
                "/unix"
        ).flatMap(s -> {
            try {
                new MultiAddress(s);
                return Stream.of(s);
            } catch (Exception e) {
                return Stream.empty();
            }
        }).collect(Collectors.toList());
        if (parsed.size() > 0)
            throw new IllegalStateException("Parsed invalid MultiAddresses: "+parsed);
    }

    @Test
    public void succeeds() {
        List<String> failed = Stream.of(
                "/ip4/1.2.3.4",
                "/ip4/0.0.0.0",
                "/ip6/::1",
                "/ip6/2601:9:4f81:9700:803e:ca65:66e8:c21",
                "/onion/timaq4ygg2iegci7:1234",
                "/onion/timaq4ygg2iegci7:80/http",
                "/udp/0",
                "/tcp/0",
                "/sctp/0",
                "/udp/1234",
                "/tcp/1234",
                "/dns4/ipfs.io",
                "/dns6/ipfs.io",
                "/dnsaddr/ipfs.io",
                "/sctp/1234",
                "/udp/65535",
                "/tcp/65535",
                "/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSupNKC",
                "/ipfs/zdpuAnNyxJR8tigBDe5FM2PfMq1u7fKSCsTmb4m3iJZcCq8yB",
                "/ipfs/zdpuAnNyxJR8tigBDe5FM2PfMq1u7fKSCsTmb4m3iJZcCq8yB/p2p-circuit",
                "/ip4/127.0.0.1/tcp/4001/p2p/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ",
                "/udp/1234/sctp/1234",
                "/udp/1234/udt",
                "/udp/1234/utp",
                "/tcp/1234/http",
                "/tcp/1234/https",
                "/tcp/1234/ws",
                "/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSupNKC/tcp/1234",
                "/ip4/127.0.0.1/udp/1234",
                "/ip4/127.0.0.1/udp/0",
                "/ip4/127.0.0.1/tcp/1234",
                "/ip4/127.0.0.1/tcp/1234/",
                "/ip4/127.0.0.1/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSupNKC",
                "/ip4/127.0.0.1/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSupNKC/tcp/1234",
                "/unix/a/b/c/d/e",
                "/unix/stdio",
                "/ip4/1.2.3.4/tcp/80/unix/a/b/c/d/e/f",
                "/ip4/127.0.0.1/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSupNKC/tcp/1234/unix/stdio"
        ).flatMap(s -> {
            try {
                new MultiAddress(s);
                return Stream.empty();
            } catch (Exception e) {
                e.printStackTrace();
                return Stream.of(s);
            }
        }).collect(Collectors.toList());
        if (failed.size() > 0)
            throw new IllegalStateException("Failed to construct MultiAddresses: "+failed);
    }

    @Test
    public void equalsTests() {
        MultiAddress m1 = new MultiAddress("/ip4/127.0.0.1/udp/1234");
        MultiAddress m2 = new MultiAddress("/ip4/127.0.0.1/tcp/1234");
        MultiAddress m3 = new MultiAddress("/ip4/127.0.0.1/tcp/1234");
        MultiAddress m4 = new MultiAddress("/ip4/127.0.0.1/tcp/1234/");

        if (m1.equals(m2))
            throw new IllegalStateException("Should be unequal!");

        if (m2.equals(m1))
            throw new IllegalStateException("Should be unequal!");

        if (!m2.equals(m3))
            throw new IllegalStateException("Should be equal!");

        if (!m3.equals(m2))
            throw new IllegalStateException("Should be equal!");

        if (!m1.equals(m1))
            throw new IllegalStateException("Should be equal!");

        if (!m2.equals(m4))
            throw new IllegalStateException("Should be equal!");

        if (!m4.equals(m3))
            throw new IllegalStateException("Should be equal!");
    }

    @Test
    public void stringToBytes() {
        BiConsumer<String, String> test = (s, h) -> {
            if (!Arrays.equals(new MultiAddress(s).getBytes(), fromHex(h))) throw new IllegalStateException(s + " bytes != " + new MultiAddress(fromHex(h)));
        };

        test.accept("/ip4/127.0.0.1/udp/1234", "047f0000011104d2");
        test.accept("/ip4/127.0.0.1/tcp/4321", "047f0000010610e1");
        test.accept("/ip4/127.0.0.1/udp/1234/ip4/127.0.0.1/tcp/4321", "047f0000011104d2047f0000010610e1");
    }

   @Test
    public void bytesToString() {
        BiConsumer<String, String> test = (s, h) -> {
            if (!s.equals(new MultiAddress(fromHex(h)).toString())) throw new IllegalStateException(s + " != " + new MultiAddress(fromHex(h)));
        };

        test.accept("/ip4/127.0.0.1/udp/1234", "047f0000011104d2");
        test.accept("/ip4/127.0.0.1/tcp/4321", "047f0000010610e1");
        test.accept("/ip4/127.0.0.1/udp/1234/ip4/127.0.0.1/tcp/4321", "047f0000011104d2047f0000010610e1");
    }

   @Test
    public void dns() {
       String dns = "mydomain.com";
       int port = 5001;
       String address = "/dns6/"+dns+"/tcp/"+port+"/https";
       MultiAddress multiAddress = new MultiAddress(address);
       
       Assert.assertEquals("host should be equal to " + dns, dns, multiAddress.getHost());
       Assert.assertEquals("port should be equal to " + port, port, multiAddress.getTCPPort());
    }

    public static byte[] fromHex(String hex) {
        if (hex.length() % 2 != 0)
            throw new IllegalStateException("Uneven number of hex digits!");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i=0; i < hex.length()-1; i+= 2)
            bout.write(Integer.valueOf(hex.substring(i, i+2), 16));
        return bout.toByteArray();
    }
}
