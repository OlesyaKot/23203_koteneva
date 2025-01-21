#include "field.h"

#include <QWidget>
#include <QPainter>
#include <QMouseEvent>
#include <QTimer>

#include "engine.h"

Field::Field(int field_length_c, int field_height_c, QWidget* parent = nullptr) : QWidget(parent),
                                                                                length_c(field_length_c),
                                                                                height_c(field_height_c),
                                                                                engine(length_c, length_c * height_c),
                                                                                length_p(length_c * CELL_SIZE),
                                                                                height_p(height_c * CELL_SIZE){
    setMinimumSize(length_p + BASE_OFFSET, height_p + BASE_OFFSET);
    timer.setInterval(500);
    connect(&timer, &QTimer::timeout, this, &Field::NextStep);
};

void Field::paintEvent( [[maybe_unused]] QPaintEvent* event){
    QPainter p(this);
    p.setPen(Qt::black);

    offset_h = (width() - length_p) / 2;
    offset_v = (height() - height_p) / 2;
    for (int i = offset_h; i <= length_p + offset_h; i += CELL_SIZE){
        p.drawLine(i, offset_v, i, height_p + offset_v);
    }

    for (int i = offset_v; i <= height_p + offset_v; i += CELL_SIZE){
        p.drawLine(offset_h, i, length_p + offset_h, i);
    }

    field = engine.GetField();
    QPainter pen(this);
    for (int i = 0; i < length_c * height_c; ++i){
        int x = (i % length_c) * CELL_SIZE + offset_h;
        int y = i / length_c * CELL_SIZE + offset_v;
        if (field[i]){
            pen.fillRect(x, y, CELL_SIZE, CELL_SIZE, Qt::black);
        }
    }
}

void Field::mousePressEvent(QMouseEvent* event){
    if (event->button() == Qt::LeftButton && !IsPlay()){
        if (offset_h <= event->x() && event->x() < length_p + offset_h &&
                offset_v <= event->y() && event->y() < height_p + offset_v){
            int idx = ((event->y() - offset_v) / CELL_SIZE * length_c + (event->x() - offset_h) / CELL_SIZE);
            engine.SetSell(idx);
            update();
        }
    }
}

void Field::Reset(){
    Stop();
    engine.Reset();
    update();
}
