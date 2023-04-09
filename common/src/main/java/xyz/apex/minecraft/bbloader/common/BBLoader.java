package xyz.apex.minecraft.bbloader.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface BBLoader
{
    String ID = "bbloader";
    Logger LOGGER = LogManager.getLogger();

    default void bootstrap()
    {
    }
}
