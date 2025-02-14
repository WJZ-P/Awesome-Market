package com.wjz.awesomemarket.entity;

public class StatisticInfo {
    public double cost_money;
    public double cost_point;
    public double buy_money;
    public double buy_point;
    public int sell_count;
    public int buy_count;
    public StatisticInfo(double cost_money,double cost_point, double buy_money,double buy_point,int sell_count,int buy_count){
        this.buy_money=buy_money;
        this.buy_point=buy_point;
        this.cost_point=cost_point;
        this.cost_money=cost_money;
        this.buy_count=buy_count;
        this.sell_count=sell_count;
    }
}
