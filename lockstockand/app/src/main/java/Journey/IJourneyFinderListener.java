package Journey;
import java.util.List;

//this code was taken ands adapted from https://github.com/hiepxuan2008/GoogleMapDirectionSimple/tree/master/app/src/main/java/Modules

public interface IJourneyFinderListener {
    void onDirectionFinderStart(); //calls method onDirectionFinderStart
    void onDirectionFinderSuccess(List<Journey> route);//list of jounrney route getting sent to onDirectionFinderSuccess
}
