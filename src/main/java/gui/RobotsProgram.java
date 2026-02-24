package gui;

import java.awt.Frame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Класс для запуска приложения
 */

public class RobotsProgram
{
    /**
     * Запускает главное окно, с установкой стиля
     * @param args
     */
    public static void main(String[] args) {
      try {

          //NimbusLookAndFeel - механизм из Java Swing, типо набор плагинов, который нужны для оформления
          // типо look - это как должно выглядит, а feel - реакции приложения на взаимодейтсвия пользователя
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //
        //ниже альтеранитвные варианты
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); //стиль по умолчанию джавы
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // системный стиль
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //настройка единого стиля для всех устройств
      } catch (Exception e) {
        e.printStackTrace();
      }

        /**
         * Запуск приложения в потоке с обработкой событий
         */
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = new MainApplicationFrame();
        frame.pack();
        frame.setVisible(true); //отображкние окна
        frame.setExtendedState(Frame.MAXIMIZED_BOTH); //разворачивание на весь экран
      });
    }}
