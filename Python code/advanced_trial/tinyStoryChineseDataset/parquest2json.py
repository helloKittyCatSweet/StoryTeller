import pandas as pd
import glob
import os
import sys
import json

def merge_parquet_files_to_json(folder_path, json_file_path):
    """
    Merges all Parquet files in a folder into a single JSON file.

    :param folder_path: Path to the folder containing Parquet files.
    :param json_file_path: Path to the output JSON file.
    """
    try:
        # 使用glob模块找到文件夹中所有的Parquet文件
        parquet_files = glob.glob(os.path.join(folder_path, '*.parquet'))

        # 检查是否找到Parquet文件
        if not parquet_files:
            print("No Parquet files found in the specified folder.")
            return

        # 合并多个Parquet文件到一个DataFrame
        merged_df = pd.concat([pd.read_parquet(file) for file in parquet_files])

        # 将合并后的DataFrame转换为JSON格式的字符串
        # 使用ensure_ascii=False以保留非ASCII字符
        json_str = merged_df.to_json(orient='records', lines=True, force_ascii=False)

        # 保存JSON字符串到文件
        with open(json_file_path, 'w', encoding='utf-8') as json_file:
            json_file.write(json_str)
        print(f"All Parquet files in '{folder_path}' have been successfully merged and saved to '{json_file_path}'.")
    except Exception as e:
        print(f"An error occurred: {e}")

def main():
    if len(sys.argv) != 3:
        print("Usage: python script.py <folder_path> <path_to_json_file>")
        sys.exit(1)

    folder_path = sys.argv[1]
    json_file_path = sys.argv[2]

    merge_parquet_files_to_json(folder_path, json_file_path)

if __name__ == "__main__":
    main()