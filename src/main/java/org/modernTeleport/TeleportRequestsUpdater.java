package org.modernTeleport;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class TeleportRequestsUpdater extends BukkitRunnable {
    ModernTeleport plugin;


    public TeleportRequestsUpdater(ModernTeleport plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(!plugin.NewRequests.isEmpty()){
            // 若有新增请求，则此处需要上锁=w=
            synchronized (plugin.NewRequests){
                // 添加待新增请求
                plugin.Requests.addAll(plugin.NewRequests);
                // 清空要新增的请求
                plugin.NewRequests.clear();
            }
        }
        for(TeleportRequest request: plugin.Requests){
            switch (request.status) {
                case Disabled:
                    plugin.DeadRequests.add(request);
                    continue;
                case Waiting:
                    RequestWaiting(request);
                    continue;
                case Active:
                    if(request.lifetime == 0){
                        request.Disable();
                        plugin.DeadRequests.add(request);
                        request.requester.sendMessage(plugin.getConfig().getString("Request-Timeout"));
                        request.target.sendMessage(plugin.getConfig().getString("Request-Timeout"));
                    }
                    request.TimePass();
                    if(request.maxtime == request.lifetime*5){
                        request.target.getWorld().playSound(request.target, Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                        request.requester.getWorld().playSound(request.requester, Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                        request.requester.sendMessage(
                                Objects.requireNonNull(plugin.getConfig().getString("Request-Will-Timeout"))
                                        .replace("%lifetime%",String.valueOf(request.lifetime)));
                        request.target.sendMessage(
                                Objects.requireNonNull(plugin.getConfig().getString("Request-Will-Timeout"))
                                        .replace("%lifetime%",String.valueOf(request.lifetime)));
                    }
            }
        }
        // 此处不需要上锁，因为DeadRequests只在RequestUpdater内部进行操作
        // 删除所有结束的请求
        if(!plugin.DeadRequests.isEmpty()){
            plugin.Requests.removeAll(plugin.DeadRequests);
            plugin.DeadRequests.clear();
        }
    }

    private void RequestWaiting(TeleportRequest request) {
        if(request.delay == 0){
            // 当延迟为0时
            if(request.type==RequestType.Go){
                request.requester.teleport(request.target);
                // GO:请求者->目标
                request.requester.getWorld().playSound(request.requester, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1,1);
                request.target.getWorld().playSound(request.target, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1,1);
                request.Disable();
                plugin.DeadRequests.add(request);
            } else if (request.type == RequestType.Come) {
                // Come:目标->请求者
                request.target.teleport(request.requester);
                request.requester.getWorld().playSound(request.requester, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1,1);
                request.target.getWorld().playSound(request.target, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1,1);
                request.Disable();
                plugin.DeadRequests.add(request);
            }
        } else{
            // 延迟未结束就-1 tick
            request.DelayTimePass();
        }
    }

    public void start(){
        // 每Tick更新一次Requests
        this.runTaskTimer(plugin,0L,1L);
    }
}
