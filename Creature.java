package maze;

public class Creature {

    //private int x;
    //private int y;
    private String type;    //identifier of creature
    private int hp, ac, size, distToP, speed, ATK, dam, damVar, hitDice;
    private boolean visible, selected, melee, ai;
    //private int[] scores = new int[6];
    //private int[] mods = new int[6];

    Creature() {
        hp = 50;
        ac = 13;
        speed = 1;
        visible = false;
        selected = false;
        melee = true;
    }

    Creature(String type, int hp, int ac, boolean melee) {//int speed, int ATK, int dam, int damVar, 
        //this.x = x;
        //this.y = y;        
        this.type = type;
        this.hp = hp;
        this.ac = ac;
        //this.size = size;
        this.speed = speed;
        this.melee = melee;
        visible = false;
        selected = false;
        distToP = 0;
    }

    public String getType() {
        return type;
    }

    public int getAC() {
        return ac;
    }
    
    public int getHP() {
        return hp;
    }
    
    public boolean getVisible() {
        return visible;
    }
    
    public int getDist() {
        return distToP;
    }
    
    public boolean getSelect() {
        return selected;
    }
    
    public boolean getMelee() {
        return melee;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public int getATK() {
        return ATK;
    }

    public int getDam() {
        return dam;
    }
    
    public int getDamVar() {
        return damVar;
    }
    
    public void loseHP(int change) {
        hp -= change;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setDist(int dist) {
        this.distToP = dist;
    }
    
    public void setDist(int distX, int distY) {
        this.distToP = (int) ((Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2))) + 0.5);
    }
    
    public void setSelect(boolean selected) {
        this.selected = selected;
    }
    
    public void switchWeapon() {
        melee = !melee;
    }
    /*
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void moveUp() {
        y--;
    }
    
    public void moveDown() {
        y++;
    }
    
    public void moveLeft() {
        x--;
    }
    
    public void moveRight() {
        x++;
    }
     */
}
