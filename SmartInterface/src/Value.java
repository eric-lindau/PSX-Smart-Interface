class Value {

    private String str;
    private boolean hasChanged;

    Value() {
        this.hasChanged = false;
        this.str = "";
    }

    void setStr(String str) {
        if (!this.str.equals(str))
            this.hasChanged = true;
        else
            this.hasChanged = false;
        this.str = str;
    }

    String getStr() {
        return this.str;
    }

    boolean hasChanged() {
        return this.hasChanged;
    }

}
