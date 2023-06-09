package org.nofs.server.http.dispatch;

import com.google.protobuf.ByteString;
import org.nofs.Grasscutter;
import org.nofs.config.ConfigContainer;
import org.nofs.config.Configuration;
import org.nofs.net.proto.QueryRegionListHttpRspOuterClass;
import org.nofs.net.proto.RegionSimpleInfoOuterClass;
import org.nofs.server.http.Router;
import org.nofs.utils.Crypto;
import org.nofs.utils.Utils;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.List;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/dispatch/RegionHandler.class */
public final class RegionHandler implements Router {
    @Override // emu.grasscutter.server.http.Router
    public void applyRoutes(Javalin javalin) {
        javalin.get("/query_region_list", RegionHandler::queryRegionList);
    }

    private static void queryRegionList(Context ctx) {
        List<RegionSimpleInfoOuterClass.RegionSimpleInfo> servers = new ArrayList<>();
        List<String> usedNames = new ArrayList<>();
        ArrayList<ConfigContainer.Region> configuredRegions = new ArrayList<>(List.of(Configuration.DISPATCH_INFO.regions));
        configuredRegions.forEach(region -> {
            if (usedNames.contains(region.Name)) {
                Grasscutter.getLogger().error("Region name already in use.");
                return;
            }
            RegionSimpleInfoOuterClass.RegionSimpleInfo identifier = RegionSimpleInfoOuterClass.RegionSimpleInfo.newBuilder().setName(region.Name).setTitle(region.Title).setType(region.type).setDispatchUrl(region.DispatchUrl).build();
            usedNames.add(region.Name);
            servers.add(identifier);
        });
        byte[] customConfig = "{\"sdkenv\":\"2\",\"checkdevice\":\"false\",\"loadPatch\":\"false\",\"showexception\":\"false\",\"regionConfig\":\"pm|fk|add\",\"downloadMode\":\"0\"}".getBytes();
        Crypto.xor(customConfig, Crypto.DISPATCH_KEY);
        QueryRegionListHttpRspOuterClass.QueryRegionListHttpRsp updatedRegionList = QueryRegionListHttpRspOuterClass.QueryRegionListHttpRsp.newBuilder().addAllRegionList(servers).setClientSecretKey(ByteString.copyFrom(Crypto.DISPATCH_SEED)).setClientCustomConfigEncrypted(ByteString.copyFrom(customConfig)).setEnableLoginPc(true).build();
        ctx.result(Utils.base64Encode(updatedRegionList.toByteString().toByteArray()));
        Grasscutter.getLogger().info(String.format("[Dispatch] Client %s request: query_region_list", ctx.ip()));
    }
}
