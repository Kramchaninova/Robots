package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */

/**
 * Главное окно приложения, которое содержит еще и внутренние окна
 * Управляет созданием и видимостью окон игры, лога, и меню
 */
public class MainApplicationFrame extends JFrame
{
    //Главное окно для размещения внутренних окон (JInternalFrame)
    private final JDesktopPane desktopPane = new JDesktopPane();

    /**
     * Конструктор главного окна, типа рабочего стола, где все последующие окна будут относительно "фиксированы" на нем
     * Инициализирует размеры, создает окна лога и игры с определенным отсупом и устанавливает меню
     */
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50; //отступ от краев экрана относительно главного окна
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        //Создание и добавление окон
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar()); //Создание менюшки
        setDefaultCloseOperation(EXIT_ON_CLOSE); //Метод закрытия окна
    }

    /**
     * Создает окно лога
     * @return окно лога
     */
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource()); //Logger.getDefaultLogSource() - это источник сообщений (куда будут писаться логи)
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает"); //Пишет в лог сообщение "Протокол работает" для проверки
        return logWindow;
    }

    /**
     * Добавляет внутреннее окно на главное окно
     * @param frame
     */
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создает меню (верхнюю строку) приложения с пунктами управления,
     * @return строка меню вверху окна
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

       //сборка меню (верхней строки) из под менюшек
        menuBar.add(createLookAndFeelMenu()); //создание меню (раздела) "Режим отображения"
        menuBar.add(createTestMenu()); //создание меню (раздела) "Тесты"
        return menuBar;
    }

    /**
     * Создает меню отображение с внутренним выбором
     * @return lookAndFeelMenu - меню с выборкой
     */
    private JMenu createLookAndFeelMenu(){
        //создание меню "Режим отображения" (типо мини выборка при нажатии)
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);//должно устанавливать горячую клавишу "alt-v" для открытия этого меню
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeel()); //добавляет пункт(кнопку) в режим отображения
        lookAndFeelMenu.add(createCrossplatformLookAndFeel());
        return lookAndFeelMenu;

    }

    /**
     * Метод создания пункта, выбор меню, кнопку
     * @return systemLookAndFeel - кнопку
     */
    private JMenuItem createSystemLookAndFeel(){
        //выборка в меню "Режим отображения", содержимого меню - "Системная схема" (кнопкой что ли ее назвать)
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S); //альт+с - выбрать режим (кнопку)
        //создает эту кнопку (часть меню режима отображения)
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //переключает внешний вид на системный, а в скобках его получает сначала
            this.invalidate();//обновляет окно
        });
        return systemLookAndFeel;
    }

    /**
     * Метод создания пункта, кнопки из выбора меню...
     * @return crossplatformLookAndFeel - кнопку
     */
    private JMenuItem createCrossplatformLookAndFeel(){
        //тоже самое что и "Системная схема", но переключает на стиль универсальный стиль джава "metal"
        //он стоит он умолчанию обычно, как и в нашем случае
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    /**
     * Метод создания тестового меню
     * @return testMenu - тестовое меню
     */
    private JMenu createTestMenu(){
        //Создание раздела(меню)
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T); //горячая клавиша альт+т
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        //Создание сообщения в окно лога
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S); //альт+с - выбрать режим (кнопку)
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);
        return testMenu;
    }

    /**
     * Устанваливает настройки LookAndFeel
     * при ошибке действия не предусмотрены
     * @param className
     */
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
