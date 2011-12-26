#ifndef CXREADER_H
#define CXREADER_H

#include <QMainWindow>
#include "feedlistmodel.h"

namespace Ui {
    class CxReader;
}

class CxReader : public QMainWindow
{
    Q_OBJECT

public:
    explicit CxReader(QString baseUrl, QWidget *parent = 0);
    ~CxReader();

private:
    Ui::CxReader *ui;
    FeedListModel * feedListModel;
};

#endif // CXREADER_H
