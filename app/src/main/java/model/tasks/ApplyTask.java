package model.tasks;

public class ApplyTask {
    String filterName; // название по которому ModelTaskManager сможет понять какой применить фильтр
    // После применения ModelTaskManager отправляет repaintEvent в который помещается изображение из ImageWorker-а
    // В ImageWorker-е два изображения: исходное (loaded) и к которому применён фильтр. Соответсвенно фильтры работают с
    // исходным и сохраняют в filtered. Надеюсь понятно что делать с этим всем)
}
