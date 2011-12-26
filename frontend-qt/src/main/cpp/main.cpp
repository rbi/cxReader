#include <QtGui/QApplication>
#include <QInputDialog>
#include "cxreader.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Q_INIT_RESOURCE(ressources);

    QWidget tmp;
    bool ok;
    QString baseUrl = QInputDialog::getText(&tmp, QObject::tr("Enter base URL"), QObject::tr("Please enter the base URL of the cxReader RESTful webservice."),
                                            QLineEdit::Normal, "", &ok);
    if(!ok)
        return -1;

    CxReader w(baseUrl);
    w.show();

    return a.exec();
}
