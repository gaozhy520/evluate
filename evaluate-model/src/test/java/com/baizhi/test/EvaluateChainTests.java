package com.baizhi.test;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;
import com.baizhi.evaluate.impl.LoginCityEvaluate;
import com.baizhi.evaluate.impl.LoginDistanceEvaluate;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;
import com.baizhi.update.impl.*;
import com.baizhi.util.LogParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluateChainTests {

    public static void main(String[] args) throws ParseException {
        List<UpdateOperator> operators = new ArrayList<>();
        operators.add(new HistoryCitiesUpdater());
        operators.add(new HistoryLoginDevicesUpdater());
        operators.add(new HistoryLoginHabitUpdater());
        operators.add(new HistoryPasswordUpdater());
        operators.add(new HistoryInputFeaturesUpdater());
        operators.add(new LastGeoPointUpdater());

        UpdateChain updateChain = new UpdateChain(operators);

        String loginSuccess1 = "INFO 2019-11-25 14:11:00 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        String loginSuccess2 = "INFO 2019-11-26 15:11:00 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934640 \"123456ct\" zhengzhou \"116.4,39.5\" [1000,1500.0,1200.0] \"Mozilla/5.0 (Windows NT 6.1; MACOS) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";


        HistoryData historyData = new HistoryData();
        if (LogParser.isLoginSuccess(loginSuccess1)) {
            LoginSuccessData loginSuccessData = LogParser.parserLoginSuccessData(loginSuccess1);
            updateChain.doChain(loginSuccessData, historyData);
        }
        updateChain.reset();
        if (LogParser.isLoginSuccess(loginSuccess2)) {
            LoginSuccessData loginSuccessData = LogParser.parserLoginSuccessData(loginSuccess2);
            updateChain.doChain(loginSuccessData, historyData);
        }
        System.out.println(historyData);

        //===============================================================================
        List<EvaluateOperator> evaluates = Arrays.asList(
                new LoginCityEvaluate(),
                new LoginDistanceEvaluate(500D)
        );
        EvaluateChain evaluateChain = new EvaluateChain(evaluates);
        String evaluateLog="INFO 2019-11-26 18:11:05 app1 EVALUATE [zhangsan01] 6ebaf4ac780f40f486359f3ea6934641 \"123456ct\" shanghai \"112.42,34.16\" [900,1800.0,1300.0] \"Mozilla/5.0 (Windows NT 6.1; iphone11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        EvaluateData evaluateData = LogParser.parserEvaluateData(evaluateLog);
        EvaluateReport report = new EvaluateReport();
        evaluateChain.doEvaluate(evaluateData,historyData,report);
        System.out.println(report);
    }
}
