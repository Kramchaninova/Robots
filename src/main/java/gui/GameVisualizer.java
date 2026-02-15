package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Класс для визуализации и управления движением робота на игровом поле
 * Робот движется к цели, задаваемой кликом мыши, с ограничениями скорости
 * и угловой скорости
 */
public class GameVisualizer extends JPanel
{
    //таймер для генерации событий обновления
    private final Timer timer = initTimer();
    
    //инициализует таймер типо он работает, пока есть событие, например у робота цель
    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    //координты, робота которые могут ищменять
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100; 
    private volatile double robotDirection = 0; 

    //координаты цели
    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    //максимальная линейная скорость
    private static final double MAX_VELOCITY = 0.1;
    //максимальная углавая скорость робота
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    /**
     * Конструктор визуализации игры
     * Создает визуализатор игры с таймерами для обновления
     * отображения и модели движения
     */
    public GameVisualizer() 
    {
        //таймер для перерисовки
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();//запросить перерисовку
            }
        }, 0, 50);//задержки

        //обвновление робота
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();//обвноить позицию робота
            }
        }, 0, 10);

        //обработчик кликов мышки
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setTargetPosition(e.getPoint());//установить новую цель, перед эти получить
                repaint(); //перерировать
            }
        });
        setDoubleBuffered(true); //двойная буферизация, обычно используется для плавной анимации
    }

    /**
     * устанавливает новую позицию цели
     * @param p точка с коррдинатами
     */
    protected void setTargetPosition(Point p)
    {
        targetPositionX = p.x;
        targetPositionY = p.y;
    }

    /**
     * запускает перерисовку компонента в потоке обработки событий.
     */
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    /**
     * вычисляет растояние между точками
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return растояние между точками
     */
    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * вычисляет угол точки от from к to
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return нормализованный угол в радианах от y до х осей
     */
    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * обновляет модель движения робота
     * вычисляет скорость и направление к цели и перемещает робота
     */
    protected void onModelUpdateEvent()
    {
        double distance = distance(targetPositionX, targetPositionY, 
            robotPositionX, robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                targetPositionX, targetPositionY);
        double angularVelocity = 0;
        //нужно повернуть на право (против часовой стрелки)
        if (angleToTarget > robotDirection)
        {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        //нужно повернуть на лево
        if (angleToTarget < robotDirection)
        {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        //перемещение
        moveRobot(velocity, angularVelocity, 10);
    }

    /**
     * ограничивает значение заданным диапазоном
     * @param value
     * @param min
     * @param max
     * @return
     */
    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    /**
     * перемещение робота с линейной и угловой скоростями в течении определенного времени
     * @param velocity линейная скорость
     * @param angularVelocity угловая скорость
     * @param duration длительность движения в мс
     */
    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        //вычисление новой позиции с учетом криволинейного движения
        double newX = robotPositionX + velocity / angularVelocity * 
            (Math.sin(robotDirection  + angularVelocity * duration) -
                Math.sin(robotDirection));
        //если новое значение х не равно бесконечность (деление на 0, те angularVelocity=0) то криволиненый
        //иначе прямолинейно
        if (!Double.isFinite(newX))//isFinite - бесконечность
        {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }
        double newY = robotPositionY - velocity / angularVelocity * 
            (Math.cos(robotDirection  + angularVelocity * duration) -
                Math.cos(robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        robotPositionX = newX;
        robotPositionY = newY;
        double newDirection = asNormalizedRadians(robotDirection + angularVelocity * duration); 
        robotDirection = newDirection;
    }

    /**
     *  Нормаолизация диапазона от 0 до 2ПИ
     * @param angle угол в радианах
     * @return нормализованный угол
     */
    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    /**
     * округление для ближайшего целого
     * @param value округляемое значение
     * @return округленное значение
     */
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    /**
     * отрисовка робота и цели
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g)
    {
        //сначла рисуем то что должно быть по умолчанию, типо фон и тд
        super.paint(g);
        //преобразуем в 2д графику отчасти для поворотов
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(robotPositionX), round(robotPositionY), robotDirection);//рисуем робота
        drawTarget(g2d, targetPositionX, targetPositionY);//рисуем цель
    }

    /**
     * рисует закрашенный овал с центром в заданной точке
     * @param g графика
     * @param centerX
     * @param centerY
     * @param diam1 диаметр по горизотали
     * @param diam2 диаметр по вертикали
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * рисует контур этого овала с центром в заданной точке
     * @param g графика
     * @param centerX
     * @param centerY
     * @param diam1 диаметр по горизонтали
     * @param diam2 диаметр по вертикали
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * рисует овального робота
     * @param g графика
     * @param x центр робота
     * @param y центр робота
     * @param direction направление робота
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = round(robotPositionX); 
        int robotCenterY = round(robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }

    /**
     * рисует цель, те зеленую точку
     * @param g графика
     * @param x координата цели
     * @param y координата цели
     */
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
