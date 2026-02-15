package gui;

import javax.swing.*;
import java.awt.*;
/**
 * Внутреннее окно для отображения игрового поля с роботом.
 * Содержит визуализатор игры GameVisualizer.
 */
public class GameWindow extends JInternalFrame
{
    //визуализатор игрового процесса
    private final GameVisualizer gameVisualizer;

    /**
     * Конструктор
     * Создает игровое окно с панелью визуализации
     */
    public GameWindow() 
    {
        //вызов конструктора родительского класс (JInternalFrame),
        //где создается окно с заголовком "Игровое поле", и разрешегие на изменение размера,
        //закрытия, сворачивания, разворачивания окна
        super("Игровое поле", true, true, true, true);
        //GameVisualizer - это специальная панель (JPanel), которая умеет рисовать вот в нашем случае
        //робота и цели, обрабатывает движения робота, реагирует на клики мышки
        gameVisualizer = new GameVisualizer(); //создание вызуализатора игры
        JPanel panel = new JPanel(new BorderLayout());//так же создается панель как в логах
        panel.add(gameVisualizer, BorderLayout.CENTER);//на эту панель на центр закрепляется визуализатор
        getContentPane().add(panel); //получаем содержимое окна и кладем туда нашу панель
        pack(); //автоподгон окна по содержимому
    }
}
