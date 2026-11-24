package net.aeternum.eu4MapUtil.map;

import net.aeternum.ironj.util.math.vec.Vec3i;

public class Province {
    public int provinceId;
    public String provinceName;
    public Vec3i rgb;

    public Province(int provinceId, Vec3i rgb, String provinceName) {
        this.provinceId = provinceId;
        this.rgb = rgb;
        this.provinceName = provinceName;
    }
    public Province (int provinceId, int red,  int green, int blue, String provinceName) {
        this.provinceId = provinceId;
        this.rgb = new Vec3i(red, green, blue);
        this.provinceName = provinceName;
    }
}
