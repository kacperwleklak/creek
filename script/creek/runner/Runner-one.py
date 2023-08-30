import os
import subprocess
import time
from datetime import datetime

from tqdm import tqdm

CAB_PROBABILITIES = [0.5]
OPERATIONS_COUNTS = [1000]
HOST = "localhost"
SERVER_PORT_BASE = 8080
PG_PORT_BASE = 5432
COM_PORT_BASE = 10000
NODES = [3]
DEBUG = False


def run_node(host: str, port: int, pg_port: int, server_port: int, node_id: int, nodes: list[str],
             _cab_probability: float, ops_cnt: int) -> subprocess.Popen:
    my_env = os.environ.copy()
    my_env["COMMUNICATION_REPLICAS_PORT"] = str(port)
    my_env["COMMUNICATION_REPLICAS_HOST"] = host
    my_env["COMMUNICATION_REPLICAS_ID"] = str(node_id)
    my_env["SERVER_PORT"] = str(server_port)
    my_env["PG_PORT"] = str(pg_port)
    my_env["COMMUNICATION_REPLICAS_NODES"] = ','.join(nodes)
    my_env["CAB_PROBABILITY"] = str(_cab_probability)
    my_env["LOG_LEVEL"] = "debug" if DEBUG else "info"
    my_env["DBNAME"] = "mem:creek"
    log_file_name = CREEK_LOGS_FILE_PATTERN.format(total_nodes=len(nodes), node_id=node_id,
                                                   cab_prob=float_to_str(_cab_probability), ops_count=ops_cnt)
    # with open(log_file_name, "w") as log_file:
    return subprocess.Popen(["C:\\Program Files\\graalvm\\bin\\java.exe",
                             "-Xmx4G", "-Xms2G", "-jar",
                             "C:\\Users\\Kacper\\magi\\creek2\\creek-impl\\target\\creek-impl-0.0.1-SNAPSHOT.jar"],
                            env=my_env, creationflags=subprocess.CREATE_NEW_CONSOLE)#, stdout=log_file, stderr=log_file)


def run_nodes(nodes: int, ops_cnt: int, _cab_probability: float) -> list[subprocess.Popen]:
    nodes_addresses = [HOST + ":" + str(COM_PORT_BASE + x) for x in range(1, nodes + 1)]
    return [run_node(host=HOST,
                     port=COM_PORT_BASE + node_nr,
                     pg_port=PG_PORT_BASE + node_nr,
                     server_port=SERVER_PORT_BASE + node_nr,
                     node_id=node_nr,
                     nodes=nodes_addresses,
                     _cab_probability=_cab_probability,
                     ops_cnt=ops_cnt)
            for node_nr in range(1, nodes + 1)]


def stop_nodes(running_nodes: list[subprocess.Popen]):
    for running_node in running_nodes:
        running_node.terminate()


def run_ycsb(node_props_file: str, workload_file: str, ops_count: int, node_id: int, cab_prob: float,
             total_nodes: int) -> subprocess.Popen:
    my_env = os.environ.copy()
    log_file_name = YCSB_LOGS_FILE_PATTERN.format(total_nodes=total_nodes, node_id=node_id,
                                                  cab_prob=float_to_str(cab_prob), ops_count=ops_count)
    with open(log_file_name, "w") as log_file:
        return subprocess.Popen(["C:\\Users\\Kacper\\IdeaProjects\\YCSB\\bin\\ycsb.bat",
                                 "load", "creek",
                                 "-P", workload_file,
                                 "-P", node_props_file,
                                 "-threads", str(total_nodes)],
                                env=my_env, stdout=log_file, stderr=log_file)


def run_benchmarks(benchmarks: int, operations_cnt: int, cab_prob: float) -> list[subprocess.Popen]:
    # benchmarks = 2 if benchmarks == 2 else benchmarks - 1
    workload_file_pattern = ".\\props\\w{ops_cnt}"
    node_info_file_pattern = ".\\props\\creekn{node_id}.properties"
    return [run_ycsb(node_props_file=node_info_file_pattern.format(node_id=benchmarks),
                     workload_file=workload_file_pattern.format(ops_cnt=operations_cnt),
                     ops_count=operations_cnt,
                     node_id=node_id,
                     cab_prob=cab_prob,
                     total_nodes=benchmarks)
            for node_id in range(1)]


def make_logs_dir() -> str:
    logs_output_dir = os.path.join(
        os.getcwd(),
        "logs",
        datetime.now().strftime('%Y-%m-%d_%H-%M-%S'))
    os.makedirs(logs_output_dir)
    return logs_output_dir


def ex(code: int):
    for x in started_nodes + started_benchmarks:
        x.kill()
    exit(code)


def float_to_str(number: float) -> str:
    return str(number).replace('.', '')


if __name__ == '__main__':
    progress_bar = tqdm(total=len(OPERATIONS_COUNTS) * len(CAB_PROBABILITIES) * len(NODES))
    started_nodes = []
    started_benchmarks = []
    logs_output_dir = make_logs_dir()
    YCSB_LOGS_FILE_PATTERN = logs_output_dir + "\\ycsb_{total_nodes}_o{ops_count}_cab{cab_prob}_n{node_id}.log"
    CREEK_LOGS_FILE_PATTERN = logs_output_dir + "\\creek_{total_nodes}_o{ops_count}_cab{cab_prob}_n{node_id}.log"

    for nodes_count in NODES:
        for cab_probability in CAB_PROBABILITIES:
            for operations_count in OPERATIONS_COUNTS:
                print(f'## Running nodes={nodes_count} cab_prob={cab_probability} ops={operations_count}')
                started_nodes = run_nodes(nodes_count, operations_count, cab_probability)
                time.sleep(nodes_count * 2)  # let them wake up
                started_benchmarks = run_benchmarks(nodes_count, operations_count, cab_probability)
                exit_codes = [process.wait() for process in started_benchmarks]
                for node in started_nodes:
                    node.terminate()
                if any(exit_code != 0 for exit_code in exit_codes):
                    print("Error found!")
                    ex(1)
                progress_bar.update()
                time.sleep(nodes_count)  # cool down
    progress_bar.close()
