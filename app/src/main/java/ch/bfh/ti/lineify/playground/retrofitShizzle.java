package ch.bfh.ti.lineify.playground;

import android.util.Log;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.model.Track;

public class retrofitShizzle {
    public static void run() {
        IWayPointRepository repo = DI.container().resolve(IWayPointRepository.class);
        repo.getTracks("benwasd@github").subscribe(res -> {
            for(Track x : res) {
                Log.i("Lola", x.userEmail());
                Log.i("Lola", x.identifier());
            }
        });
    }
}
