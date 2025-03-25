package org.example.controller.filters;

public abstract class IFilter {
    public IFilter(){

    }
    //отправить модели, что нужно выполнить фильтрацию
    public abstract void sendToModel();
}
