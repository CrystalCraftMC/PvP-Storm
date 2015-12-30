package com.crystalcraftmc.pvpstorm;

/**
 * This class file will handle scoring methods. It is a separate class to maintain
 * a compartmentalized plugin that will be much easier to manage.
 */
public class GameScore {
    private double damage = 0;
    private double hits = 0;

    /**
     * Add other fields as needed to create a procedure of how to calculate scores
     */
    public GameScore(double d, int h)
    {
        this.damage = d;
        this.hits = h;
    }

    double getHitCount()
    {
        return this.hits;
    }

    double getDamageCount()
    {
        return this.damage;
    }

    void setDamageCount(double cDamage) { this.damage = cDamage; }

    void setHitCount(int cHit) { this.hits = cHit; }

    //the reason we have set and up hitcount, is to optimize runtime according to needs
    void upHitCount() { this.hits++; }
}