package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.EvaluateReportItem;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

public class LoginDistanceEvaluate implements EvaluateOperator {
    private Double speedThreshold;

    public LoginDistanceEvaluate(Double speedThreshold) {
        this.speedThreshold = speedThreshold;
    }

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        Boolean result = evaluateLoginAddress(evaluateData.getCurrentTime(), evaluateData.getGeoPoint(), historyData.getLastLoginTime(), historyData.getbPoint(), speedThreshold);
        EvaluateReportItem item = new EvaluateReportItem();
        item.setKey("distance");
        item.setValue(result);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData,historyData,evaluateReport);
    }

    /**
     * // bj [116.404305,39.914658]
     * // sh [121.4975,31.242227]
     * <p>
     * 基于用户位移距离的风险判定
     *
     * @currentLoginTime
     * @aPoint 当前位置坐标
     * @bPoint 上一次登录位置坐标
     */
    public Boolean evaluateLoginAddress(long currentLoginTime, Double[] aPoint, long lastLoginTime, Double[] bPoint, Double speedThreshold) {
        // AB两点弧长距离计算公式
        // R*arccos(cos(wA)*cos(wB)*cos(jB-jA) + sin(wB)*sin(wA))
        // 6371
        // wA wB jB jA

        // nπR/180  角度 ---> 弧度
        Double wA = aPoint[1] * Math.PI / 180;
        Double wB = bPoint[1] * Math.PI / 180;
        Double jA = aPoint[0] * Math.PI / 180;
        Double jB = bPoint[0] * Math.PI / 180;

        Double distance = 6371 * Math.acos(Math.cos(wA) * Math.cos(wB) * Math.cos(jB - jA) + Math.sin(wB) * Math.sin(wA));

        // 1200km  500KM/h
        double totalTime = distance / speedThreshold;

        System.out.println("A点到B点的距离:" + distance + "\t 理论位移时长:" + totalTime);
        // 2.4小时

        // 理论时间 > 实际时间
        if ((totalTime * 3600 * 1000) > (currentLoginTime - lastLoginTime)) {
            return true;
        } else {
            return false;
        }
    }
}
