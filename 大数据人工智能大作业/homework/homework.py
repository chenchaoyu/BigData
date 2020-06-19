import os

import pandas as pd
from sklearn import tree
from sklearn.linear_model import LinearRegression
from sklearn.metrics import explained_variance_score, mean_absolute_error, \
    mean_squared_error, r2_score, median_absolute_error
from sklearn.model_selection import train_test_split

if __name__ == '__main__':
    train_data_path = os.path.join('../homework', 'train.csv')
    test_data_path = os.path.join('../homework', 'test.csv')
    train_df = pd.read_csv(train_data_path)

    condition = train_df.apply(lambda s: pd.to_numeric(s, errors='coerce').notnull().all()).to_dict()
    train_df = train_df[[k for k, v in condition.items() if v]]


    test_df = pd.read_csv(test_data_path)


    Y = train_df.get('SalePrice')
    X = train_df.drop('SalePrice', axis=1)
    # X = train_df[['LotArea', 'OverallQual', 'OverallCond', 'BsmtFinSF1', 'TotalBsmtSF', '1stFlrSF', '2ndFlrSF', 'GrLivArea', 'GarageArea']].values
    # X = X[:, np.newaxis]
    # y_test = test_df.get('SalePrice')
    # x_test = test_df.drop('SalePrice', axis=1)
    X_train, x_test, y_train, y_test = train_test_split(X, Y, test_size=0.2)


    linearRegressor = LinearRegression()
    decisionTreeRegressor = tree.DecisionTreeRegressor()


    linearRegressor.fit(X_train, y_train)
    decisionTreeRegressor.fit(X_train, y_train)

    ##数据预测==============
    y_pred_linear = linearRegressor.predict(x_test)
    y_pred_decisionTree = decisionTreeRegressor.predict(x_test)
    y_test = y_test.values

    # 写入csv文件
    x_test['pred_price'] = y_pred_decisionTree
    x_test.to_csv("../homework/result.csv", sep=',', header=True, index=True)

    ##评分==============
    linear_score = r2_score(y_test, y_pred_linear)

    y_test = y_test / 10000
    y_pred_decisionTree = y_pred_decisionTree / 10000

    mean_absolute_value = mean_absolute_error(y_test, y_pred_decisionTree)
    print(f"平均绝对误差:{mean_absolute_value}")

    mean_squared_value = mean_squared_error(y_test, y_pred_decisionTree)
    print(f"均方差:{mean_squared_value}")

    median_absolute_value = median_absolute_error(y_test, y_pred_decisionTree)
    print(f"中值绝对误差:{median_absolute_value}")

    explained_variance_value = explained_variance_score(y_test, y_pred_decisionTree)
    print(f"可解释方差:{explained_variance_value}")

    r2_value = r2_score(y_test, y_pred_decisionTree)
    print(f"r2值:{r2_value}")


