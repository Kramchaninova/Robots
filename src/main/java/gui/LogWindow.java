package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;

/**
 * Окно логов, которое находится внутри главного окна
 * Оно отображает протоколы работы приложения
 * Реализует LogChangeListener для автообнавлений при новых записях в логах
 */
//LogChangeListener - это интерфейс-слушатель, который используется для отслеживания изменений в логе
public class LogWindow extends JInternalFrame implements LogChangeListener
{
    //источник отображения логов
    private LogWindowSource logSource;
    //полк для ввода содержимого лога
    private TextArea logContent;

    /**
     * Конструуктор окна логов
     * Инициализирует окно с заголовком "Протокол работы", настраивает
     * текстовую область для вывода логов и регистрирует окно как
     * слушатель изменений источника логов
     * При создании автоматически
     * загружает и отображает все имеющиеся сообщения
     *
     * @param logSource источник сообщений для отображения в окне
     */
    public LogWindow(LogWindowSource logSource)
    {
        //вызов конструктора родительского класс (JInternalFrame),
        //где создается окно с заголовком "Протокол работы", и разрешегие на изменение размера,
        //закрытия, сворачивания, разворачивания окна
        super("Протокол работы", true, true, true, true);
        this.logSource = logSource;
        this.logSource.registerListener(this); //подписка на обновления, this - это сам LogWindow (он реализует LogChangeListener)
        this.logContent = new TextArea(""); //текствое поле, где пустая строка - начальное содержимое
        this.logContent.setSize(200, 500);//очевидные размеры

        // BorderLayout() - типо правило размвещение объекста относительно панели, типо центр, вверх, низ (там 5 зон)
        JPanel panel = new JPanel(new BorderLayout());//создаем панель (контейнер), это то что будет держать текствое поле
        panel.add(logContent, BorderLayout.CENTER);//кладем текстовое поле на панель, причем по центру
        getContentPane().add(panel); //ставим панель в окно
        pack(); //метод Swing, с автоподгоном размера по объектам внутри окна
        updateLogContent();
    }

    /**
     * Обновляет содержимое текствого поля
     * Формирует строку из всех записей
     */
    private void updateLogContent()
    {
        //StringBuilder - это типо "черновик", где мы собираем текст
        StringBuilder content = new StringBuilder();
       //короче собираем каждый лог в единую строку с энторами - "черновик"
        for (LogEntry entry : logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        //устанавливаем собранный текст в текстовое поле, изначально превратив ее в строку
        logContent.setText(content.toString());
        logContent.invalidate(); //помечаем, что содержимое устарело, нужно обновить
    }

    /**
     * Обработчик изменения лога
     * Вызывается сам при добавлении новой записи
     * Запускает обновление отображения в потоке обработки событий.
     */
    @Override
    public void onLogChanged()
    {
        //просим Swing: "Обнови интерфейс, когда будет удобно"
        EventQueue.invokeLater(this::updateLogContent);
    }
}
