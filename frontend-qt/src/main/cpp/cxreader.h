#ifndef CXREADER_H
#define CXREADER_H

#include <QMainWindow>
#include <QNetworkReply>
#include <QAuthenticator>
#include <QItemSelection>
#include "config.h"
#include "feedlistmodel.h"
#include "feedentrylistmodel.h"

namespace Ui {
    class CxReader;
}

class CxReader : public QMainWindow
{
    Q_OBJECT

public:
    explicit CxReader(Config * config, QWidget *parent = 0);
    ~CxReader();
private:
    Config * config;
    Ui::CxReader *ui;
    FeedListModel * feedListModel;
    FeedEntryListModel * feedEntryListModel;
    QNetworkAccessManager * _manager;
private slots:
    void feedChanged(QItemSelection selected, QItemSelection deselected);
    void feedEntryChanged(QItemSelection selected, QItemSelection deselected);
    void unreadOnlyChanged(bool status);
};

#endif // CXREADER_H
