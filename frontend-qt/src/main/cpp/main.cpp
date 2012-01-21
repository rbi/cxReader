#include <QtGui/QApplication>
#include <QInputDialog>
#include "cxreader.h"
#include "config.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Q_INIT_RESOURCE(ressources);

    //QWidget tmp;
//    QString baseUrl = QInputDialog::getText(&tmp, QObject::tr("Enter base URL"), QObject::tr("Please enter the base URL of the cxReader RESTful webservice."),
//                                            QLineEdit::Normal, "", &ok);
    Config config;
    if(config.exec() == QDialog::Rejected)
        return -1;

    CxReader w(&config);
    w.show();

    return a.exec();
}
