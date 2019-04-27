package eu.faircode.netguard;

/*
    This file is part of NetGuard.


*/

import com.alim.cleanmaster.*;
public class Allowed {
    public String raddr;
    public int rport;

    public Allowed() {
        this.raddr = null;
        this.rport = 0;
    }

    public Allowed(String raddr, int rport) {
        this.raddr = raddr;
        this.rport = rport;
    }
}
