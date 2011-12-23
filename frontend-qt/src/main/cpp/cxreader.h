#ifndef CXREADER_H
#define CXREADER_H

#include <QMainWindow>

namespace Ui {
    class CxReader;
}

class CxReader : public QMainWindow
{
    Q_OBJECT

public:
    explicit CxReader(QWidget *parent = 0);
    ~CxReader();

private:
    Ui::CxReader *ui;
};

#endif // CXREADER_H
