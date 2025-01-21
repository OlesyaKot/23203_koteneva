#ifndef FIELD_H
#define FIELD_H

#include <QWidget>
#include <QTimer>
#include <vector>

#include "engine.h"

class Field : public QWidget{
public:
    Field(int field_length_c, int field_height_c, QWidget* parent);

    Field (const Field&) = delete;
    Field& operator=(const Field&) = delete;

    void paintEvent( [[maybe_unused]] QPaintEvent* event) override;

    void mousePressEvent(QMouseEvent* event) override;

    void mouseMoveEvent( [[maybe_unused]] QMouseEvent* event) override;

    void mouseReleaseEvent(QMouseEvent* event) override;

    void Reset();

    void SetPaint(){
        is_paint = false;
    }

    void Play(){
        timer.start();
    }

    void Stop(){
        timer.stop();
    }

    bool IsPlay(){
        return timer.isActive();
    }

    ~Field() = default;

public slots:
    void NextStep(){
        engine.Game();
        update();
    }

private:
    static constexpr int CELL_SIZE = 20;
    static constexpr int BASE_OFFSET = 10;
    QTimer timer;
    std::vector<bool> field;
    int length_c;
    int height_c;
    Engine engine;
    int length_p;
    int height_p;
    int offset_h = 0;
    int offset_v = 0;
    bool is_paint = false;
};

#endif // FIELD_H
