package org.firstinspires.ftc.teamcode.helpers;

public enum Curve {
    LINEAR {
        @Override public double apply(double x, double p1, double p2, double a) { return x; }
    },
    CUBIC_BEZIER {
        @Override public double apply(double x, double p1, double p2, double a) {
            double t = Math.abs(x), it = 1 - t;
            return Math.signum(x) * (3 * it * it * t * p1 + 3 * it * t * t * p2 + t * t * t);
        }
    },
    SMOOTHSTEP {
        @Override public double apply(double x, double p1, double p2, double a) {
            double t = Math.abs(x);
            return Math.signum(x) * (t * t * (3 - 2 * t));
        }
    },
    EXPONENTIAL {
        @Override public double apply(double x, double p1, double p2, double a) {
            if (a == 0) return x;
            double t = Math.abs(x);
            return Math.signum(x) * ((Math.exp(a * t) - 1) / (Math.exp(a) - 1));
        }
    },
    QUINTIC {
        @Override public double apply(double x, double p1, double p2, double a) {
            double t = Math.abs(x);
            return Math.signum(x) * (t * t * t * (t * (t * 6 - 15) + 10));
        }
    };

    public abstract double apply(double x, double p1, double p2, double a);

    public static double lerp(double s, double e, double t) { return s + (e - s) * t; }
    public static double log(double s, double e, double t) {
        return (s <= 0 || e <= 0) ? lerp(s, e, t) : s * Math.pow(e / s, t);
    }
}
