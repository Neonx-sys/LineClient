package im.rez.utils.animate;


import im.rez.utils.math.MathUtil;

public class AnimateUtil {
    long mc;
    float anim;
    float anim2;
    public float to;
    public float speed;
    public AnimateUtil(float anim, float to, float speed){
        this.anim = anim;
        this.to = to;
        this.speed = speed;
        mc = System.currentTimeMillis();
    }
    public float getAnim() {
        int count = (int) ((System.currentTimeMillis() - mc) / 5);
        if (count > 0){
            mc = System.currentTimeMillis();
        }
        for (int i = 0; i < count; i++) {
            anim = MathUtil.lerp(anim, to, speed);
        }
        return anim;

    }
    public float getAnimHarp() {
        int count = (int) ((System.currentTimeMillis() - mc) / 5);
        if (count > 0){
            mc = System.currentTimeMillis();
        }
        for (int i = 0; i < count; i++) {
            anim = MathUtil.harp(anim, to, speed);
        }
        return anim;

    }
    public void reset(){
        mc = System.currentTimeMillis();
    }


    public void setAnim(float anim) {
        this.anim = anim;
        this.anim2 = anim;
        mc = System.currentTimeMillis();
    }
}
