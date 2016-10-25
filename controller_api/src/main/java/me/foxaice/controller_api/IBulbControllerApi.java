package me.foxaice.controller_api;

import java.io.IOException;

public interface IBulbControllerApi extends IControllerApi {
    void setBrightnessOfCurrentGroup(int brightness) throws IOException;
    void setBrightnessOfGroup(int group, int brightness) throws IOException, InterruptedException;
    void setColorOfCurrentGroup(int color) throws IOException;
    void setColorOfGroup(int group, int color) throws IOException, InterruptedException;
    void toggleDiscoModeOfCurrentGroup() throws IOException;
    void toggleDiscoModeOfGroup(int group) throws IOException, InterruptedException;
    void speedUpDiscoModeOfCurrentGroup() throws IOException;
    void speedUpDiscoModeOfGroup(int group) throws IOException, InterruptedException;
    void speedDownDiscoModeOfCurrentGroup() throws IOException;
    void speedDownDiscoModeOfGroup(int group) throws IOException, InterruptedException;
    void powerOnGroup(int group) throws IOException;
    void powerOffGroup(int group) throws IOException;
    void setWhiteColorOfGroup(int group) throws IOException, InterruptedException;
    void setCurrentGroup(int group) throws IOException;
    void closeSockets();
}
