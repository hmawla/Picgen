package com.hmawla.picgen.builder;

import com.hmawla.picgen.adapter.PicsRenderer;
import com.hmawla.picgen.model.Pic;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.LinkedList;
import java.util.List;

public class PicRendererBuilder extends RendererBuilder<Pic> {

    public PicRendererBuilder() {
        List<Renderer<Pic>> prototypes = getRendererVideoPrototypes();
        setPrototypes(prototypes);

    }

    @Override
    protected Class getPrototypeClass(Pic content) {
        Class prototypeClass;
        prototypeClass = PicsRenderer.class;
        return prototypeClass;
    }

    private List<Renderer<Pic>> getRendererVideoPrototypes() {
        List<Renderer<Pic>> prototypes = new LinkedList<>();

        PicsRenderer liveVideoRenderer = new PicsRenderer();
        prototypes.add(liveVideoRenderer);

        return prototypes;
    }
}
