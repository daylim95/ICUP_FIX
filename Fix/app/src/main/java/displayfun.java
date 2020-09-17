import android.app.Application;

import com.example.hhj73.fix.RudenessScreenHelper;

/**
 * Created by Administrator on 2018/5/27.
 * 외부 class
 */

public class displayfun extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        new RudenessScreenHelper(this,768).activate();

        /**
         * sp -> dp 자동 변환
         * width값  = xml 디자인 할때 쓰는 가상기계의 width
         * detail:
         * https://www.jianshu.com/p/b6b9bd1fba4d
         * https://github.com/Firedamp/Rudeness
         *
         * */
    }
}
