#ifndef FEEDENTRYLISTMODEL_H
#define FEEDENTRYLISTMODEL_H

#include <QAbstractListModel>
#include "config.h"

class FeedEntryListModel  : public QAbstractListModel
{
    Q_OBJECT
public:
    explicit FeedEntryListModel(Config * config, QString id, QObject * parent);
    int rowCount(const QModelIndex &parent) const;
    QVariant data(const QModelIndex &index, int role) const;
    QVariant headerData(int section, Qt::Orientation orientation, int role) const;
private:
    QString id;
    Config * config;
};

#endif // FEEDENTRYLISTMODEL_H
