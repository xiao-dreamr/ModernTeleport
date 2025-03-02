package org.modernTeleport;

import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

public class TeleportRequest {
    Player requester;
    Player target;
    int lifetime; //单位为Tick
    int delay; //单位为Tick
    RequestStatus status = RequestStatus.Active;
    RequestType type;

    @ParametersAreNonnullByDefault
    public TeleportRequest(Player requester, Player target, int lifetime, int delay, RequestType type) {
        this.requester = requester;
        this.target = target;
        this.lifetime = lifetime * 20;
        this.delay = delay * 20;
        this.type = type;
    }

    public void TimePass() {
        this.lifetime -= 1;
    }

    public void DelayTimePass() {
        this.delay -= 1;
    }

    public void Disable(){
        this.status = RequestStatus.Disabled;
    }

    public void Wait(){
        this.status = RequestStatus.Waiting;
    }
}
