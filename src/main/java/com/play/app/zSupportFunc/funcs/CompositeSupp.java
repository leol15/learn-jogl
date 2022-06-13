package com.play.app.zSupportFunc.funcs;

import java.util.ArrayList;
import java.util.List;

import com.play.app.zSupportFunc.SupportFunc;

import org.joml.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CompositeSupp extends SupportFunc {

    private final List<SupportFunc> positive = new ArrayList<>();
    private final List<SupportFunc> negative = new ArrayList<>();

    public void add(SupportFunc f) {
        positive.add(f);
    }

    public void subtract(SupportFunc f) {
        negative.add(f);
    }

    @Override
    protected Vector3f getMax(Vector3f direction) {
        final Vector3f max = new Vector3f();
        positive.forEach(f -> max.add(f.getMaxWorld(direction)));
        final Vector3f negDir = new Vector3f(direction).mul(-1);
        negative.forEach(f -> max.sub(f.getMaxWorld(negDir)));
        return max;
    }

}
