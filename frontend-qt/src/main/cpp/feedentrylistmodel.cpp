#include "feedentrylistmodel.h"

FeedEntryListModel::FeedEntryListModel(Config * config, QString id, QObject * parent) :
    QAbstractListModel(parent), id(id), config(config)
{

}

int FeedEntryListModel::rowCount(const QModelIndex &parent) const
{
    return 1;
}

QVariant FeedEntryListModel::data(const QModelIndex &index, int role) const
{
    if(role == Qt::DisplayRole)
        return this->id;
    return QVariant::Invalid;
}

QVariant FeedEntryListModel::headerData(int section, Qt::Orientation orientation, int role) const
{
    if(role == Qt::DisplayRole)
        return tr("Entries");
    return QVariant::Invalid;
}
