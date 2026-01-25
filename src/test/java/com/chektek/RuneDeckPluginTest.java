package com.chektek;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RuneDeckPluginTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(RuneDeckPlugin.class);
        RuneLite.main(args);
    }
}