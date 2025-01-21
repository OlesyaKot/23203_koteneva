#include "mainwindow.h"
#include "ui_mainwindow.h"

#include <QApplication>
#include <QPalette>
#include <QPushButton>
#include <QSize>

MainWindow::MainWindow(QWidget* parent) : QMainWindow(parent) , ui(new Ui::MainWindow) {
    ui->setupUi(this);
    f = new Field(LENGHT, WIDTH, this);
    setCentralWidget(f);
    QSizePolicy policy(QSizePolicy::Expanding, QSizePolicy::Expanding);
    f->setSizePolicy(policy);

    QPalette palette;
    palette.setColor(QPalette::Window, Qt::white);
    this->setPalette(palette);
    this->setAutoFillBackground(true);
}

MainWindow::~MainWindow(){
    delete ui;
}

void MainWindow::on_pushButton_clicked(){
    QApplication::instance()->exit();
}

void MainWindow::on_pushButton_2_clicked(){
    if (!f->IsPlay())
        f->NextStep();
}

void MainWindow::on_pushButton_3_clicked(){
    ui->pushButton_4->setText("Play");
    f->Reset();
}

void MainWindow::on_pushButton_4_clicked(){ // play
    if (f->IsPlay()){
        ui->pushButton_4->setText("Play");
        f->Stop();
        return;
    }
    ui->pushButton_4->setText("Stop");
    f->Play();
}
