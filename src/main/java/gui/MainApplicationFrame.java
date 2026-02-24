package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //временно отключаем автозакрытие, не генерирует swing событие на закрытие
        addWindowListener(new WindowAdapter() {//вот тут перехватываем событие
            @Override
            public void windowClosing(WindowEvent e) {
                exitConfirmWindow(); //диалог, после нажатия крестика
            }
        });
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
        menuBar.add(createExitMenu()); //создание меню "Выход из приложения"
        return menuBar;
    }

    /**
     * Создание меню выхода
     * @return exitMenu - меню выхода
     */
    private JMenu createExitMenu() { // нельяз сразу сделать выход, тк JMenu умеет только показывать выборку меню
        JMenu exitMenu = new JMenu("Выход из приложения");
        exitMenu.setMnemonic(KeyEvent.VK_F5);//альт+ф
        exitMenu.add(createExitContent());
        return exitMenu;
    }

    /**
     * Создание содержимого меню
     * @return exitContent - пункт меню
     */
    private JMenuItem createExitContent() {
        JMenuItem exitСontent = new JMenuItem("Выход", KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitСontent.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        return exitСontent;
    }



    /**
     * Диалог внутри окна с подтверждением на выход
     */
    private void exitConfirmWindow() {
        String[] options = {"Да", "Нет"};
        //создаем диалог подтверждения выхода
        int result = JOptionPane.showOptionDialog(
                this,   //родительский компонент (главное окно, поверх которого мини приложение)
                "Вы действительно хотите выйти?", //сообщение
                "Подтверждение выхода", //заголовок окна
                JOptionPane.YES_NO_OPTION, //варианты ответа (Да/Нет)
                JOptionPane.QUESTION_MESSAGE, //тип сообщения (вопрос)
                null, //иконка
                options, //массив с кнопками
                options[1]  //кнопка по умолчанию - нет
        );

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
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
