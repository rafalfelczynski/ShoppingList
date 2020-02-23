package ib.edu.shoppingList;

public interface Observable {

    public void addObserver(Observer obs);
    void removeObserver(Observer obs);
    void notifyObservers(Object obj);
}
