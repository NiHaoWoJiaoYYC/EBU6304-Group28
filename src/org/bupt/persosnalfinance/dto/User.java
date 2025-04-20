package org.bupt.persosnalfinance.dto;




    public class User {
        private double[] lastQuarterAvg; // 上期花费
        private double[] thisQuarter; // 本期花费

        // Getters 和 Setters

        public double[] getLastQuarterAvg() {
            return lastQuarterAvg;
        }

        public void setLastQuarterAvg(double[] lastQuarterAvg) {
            this.lastQuarterAvg = lastQuarterAvg;
        }

        public double[] getThisQuarter() {
            return thisQuarter;
        }

        public void setThisQuarter(double[] thisQuarter) {
            this.thisQuarter = thisQuarter;
        }
    }

