"use client";
import CustomSkeleton from '@/components/custom-skeleton';
import { SearchIcon } from '@/components/icons/searchicon';
import {
    Autocomplete,
    AutocompleteItem,
    Button,
    Input,
    RadioGroup,
    Switch,
    Tab,
    Table,
    TableBody,
    TableCell,
    TableColumn,
    TableHeader,
    TableRow,
    Modal,
    ModalContent,
    ModalHeader,
    ModalBody,
    ModalFooter,
    useDisclosure,
    Chip,
    Card,
    CardBody,
    Accordion,
    AccordionItem,
    Snippet,
    User,
    CardHeader,
    Select,
    SelectItem
} from '@nextui-org/react';
import dynamic from 'next/dynamic';
import React, { useMemo } from 'react';
import { useGetAutoComplete, useGetSearch, useGetDirections } from '@/services/mapService';
import _, { set } from 'lodash';
import LocationIcon from '@/components/icons/location-icon';
import { convertStringInstantToDate, convertStringInstantToDateTime } from '@/util/dateConverter';
import { useGetListManipulatePickupPoint, useGetListRideAtThatDay, useUpdateEmployeeBus, useUpdateEmployeeRide, useUpdateEmployeeRidePickupPoint, useUpdateEmployeeStudentPickupPoint } from '@/services/employee/employeeService';
import { bus_status_map, ride_pickup_point_status_map, ride_status_map, student_pickup_point_status_map } from '@/util/constant';
import { stringify } from 'querystring';
import { validateColor } from '@/util/color';

const EmployeeMonitoring: React.FC = () => {

    // get auto complete
    const [autoCompleteQuery, setAutoCompleteQuery] = React.useState('');
    let autoCompleteParams: IAutoCompleteParams = {
        text: autoCompleteQuery,
        boundary: {
            country: 'VNM'
        }
    }
    const debounceSetAutoCompleteQuery = _.debounce((value: string) => setAutoCompleteQuery(value), 500);
    const { data: autoCompleteData, isLoading: autoCompleteLoading, error: autoCompleteError } = useGetAutoComplete(autoCompleteParams);
    const [selectedAutoCompleteData, setSelectedAutoCompleteData] = React.useState<IFeature | null>(null);

    const Map = useMemo(() => dynamic(
        () => import('@/components/map/MapEmployee'),
        {
            loading: () => <CustomSkeleton />,
            ssr: false
        }
    ), [])

    // enable click point to map
    const [enableClickMap, setEnableClickMap] = React.useState<boolean>(false);



    const [date, setDate] = React.useState<string>(convertStringInstantToDate(new Date().toISOString()));
    // get list ride at that day
    const { data: listRideAtThatDayData, isLoading: listRideAtThatDayLoading, error: listRideAtThatDayError } = useGetListRideAtThatDay(date);

    // get manipulate pickup point
    const [rideId, setRideId] = React.useState<number | null>(null);
    const { data: manipulatePickupPointData, isLoading: manipulatePickupPointLoading, error: manipulatePickupPointError } = useGetListManipulatePickupPoint(date, rideId);

    // get directions
    const useGetManipulatePickupPointDirectionParams: IDirectionsParams = {
        coordinates: manipulatePickupPointData?.result?.pickupPointWithStudents.map((item) => [item.pickupPoint.longitude, item.pickupPoint.latitude]) || []
    }
    const { data: directionsGetResponse, isLoading: directionsLoading, error: directionsError } = useGetDirections(useGetManipulatePickupPointDirectionParams);

    // update bus
    const { isOpen: isUpdateBusModalOpen, onOpen: onOpenUpdateBusModal, onOpenChange: onOpenUpdateBusModalChange } = useDisclosure();
    const [status, setStatus] = React.useState<string>(manipulatePickupPointData?.result?.bus.status || '');
    let updateEmployeeBusRequest: IEmployeeUpdateBusRequest = {
        numberPlate: manipulatePickupPointData?.result?.bus.numberPlate || '',
        status: status
    }
    const updateEmployeeBusMutation = useUpdateEmployeeBus(() => {
        onOpenUpdateBusModalChange();
    });
    const handleUpdateEmployeeBus = () => {
        if (!updateEmployeeBusRequest.numberPlate || !updateEmployeeBusRequest.status) {
            return;
        }
        updateEmployeeBusMutation.mutate(updateEmployeeBusRequest);
    }

    // update ride
    const { isOpen: isUpdateRideModalOpen, onOpen: onOpenUpdateRideModal, onOpenChange: onOpenUpdateRideModalChange } = useDisclosure();
    const [statusRide, setStatusRide] = React.useState<string>(manipulatePickupPointData?.result?.ride.status || '');
    let updateEmployeeRideRequest: IEmployeeUpdateRideRequest = {
        rideId: manipulatePickupPointData?.result?.ride.id || null,
        status: statusRide
    }
    const updateEmployeeRideMutation = useUpdateEmployeeRide(() => {
        onOpenUpdateRideModalChange();
    });
    const handleUpdateEmployeeRide = () => {
        if (!updateEmployeeRideRequest.rideId || !updateEmployeeRideRequest.status) {
            return;
        }
        updateEmployeeRideMutation.mutate(updateEmployeeRideRequest);
    }

    // update ride pickup point
    const [statusRidePickupPoint, setStatusRidePickupPoint] = React.useState<string>('');
    const [selectedPickupPointId, setSelectedPickupPointId] = React.useState<number | null>(null);
    let employeeUpdateRidePickupPointRequest: IEmployeeUpdateRidePickupPointRequest = {
        rideId: manipulatePickupPointData?.result?.ride.id || null,
        pickupPointId: selectedPickupPointId || null,
        status: statusRidePickupPoint
    }
    const { isOpen: isUpdateRidePickupPointModalOpen, onOpen: onOpenUpdateRidePickupPointModal, onOpenChange: onOpenUpdateRidePickupPointModalChange } = useDisclosure();
    const updateEmployeeRidePickupPointMutation = useUpdateEmployeeRidePickupPoint(() => {
        setStatusRidePickupPoint('');
        onOpenUpdateRidePickupPointModalChange();
    });
    const handleUpdateEmployeeRidePickupPoint = () => {
        if (!employeeUpdateRidePickupPointRequest.pickupPointId || !employeeUpdateRidePickupPointRequest.status) {
            return;
        }
        updateEmployeeRidePickupPointMutation.mutate(employeeUpdateRidePickupPointRequest);
    }

    // update student pickup point
    const [statusStudentPickupPoint, setStatusStudentPickupPoint] = React.useState<string>('');
    const [studentIds, setStudentIds] = React.useState<number[]>([]);
    const [pickupPointId, setPickupPointId] = React.useState<number | null>(null);
    const { isOpen: isUpdateStudentPickupPointModalOpen, onOpen: onOpenUpdateStudentPickupPointModal, onOpenChange: onOpenUpdateStudentPickupPointModalChange } = useDisclosure();
    let employeeUpdateStudentPickupPointRequest: IEmployeeUpdateStudentPickupPointRequest = {
        studentIds: studentIds,
        pickupPointId: pickupPointId,
        status: statusStudentPickupPoint,
        rideId: manipulatePickupPointData?.result?.ride.id || null
    }
    const updateEmployeeStudentPickupPointMutation = useUpdateEmployeeStudentPickupPoint(() => {
        setStatusStudentPickupPoint('');
        onOpenUpdateStudentPickupPointModalChange();
    });
    const handleUpdateEmployeeStudentPickupPoint = () => {
        if (!employeeUpdateStudentPickupPointRequest.studentIds.length || !employeeUpdateStudentPickupPointRequest.status) {
            return;
        }
        updateEmployeeStudentPickupPointMutation.mutate(employeeUpdateStudentPickupPointRequest);
    }



    // ui
    if (manipulatePickupPointLoading) {
        return <CustomSkeleton />
    }
    // if dont have data => return message you dont have ride today
    if (!manipulatePickupPointData?.result) {
        return <div className='w-auto h-full m-4 flex-col justify-center'>
            <Input
                className='m-2 w-1/4 bg-default-100 rounded-lg'
                type='date'
                label='Chọn ngày'
                value={date}
                onChange={(event) => {
                    setDate(event.target.value);
                }}
                variant='flat'
            />
            <Card className='m-2 w-full h-full flex justify-center'>
                <CardBody className='flex justify-center'>
                    <div className='flex justify-center'>
                        <h1 className='font-bold'>Bạn không có chuyến đi nào hôm nay!</h1>
                    </div>
                </CardBody>
            </Card>
        </div>
    }
    return (
        <div className='flex flex-col'>
            <div className='flex gap-4 items-center'>
                <Input
                    className='m-2 w-1/4 bg-default-100 rounded-lg'
                    type='date'
                    label='Chọn ngày'
                    value={date}
                    onChange={(event) => {
                        setDate(event.target.value);
                    }}
                    variant='flat'
                />
                {/* select ride id from list ride at that day */}
                <Select
                    className='m-2 w-1/4 bg-default-100 rounded-lg'
                    label='Chọn chuyến đi'
                    placeholder='Chọn chuyến đi'
                    value={rideId || ''}
                    defaultSelectedKeys={[rideId || '']}
                    onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                        setRideId(parseInt(event.target.value));
                    }}
                >
                    {
                        listRideAtThatDayData?.result?.map((item, index) => {
                            return (
                                <SelectItem key={item.id} value={item.id}>
                                    {item.id}
                                </SelectItem>
                            )
                        }) || [] // Add an empty array as a fallback for the children prop
                    }
                </Select>
            </div>
            <div className='flex justify-between  flex-col sm:flex-row'>
                {/* card bus info */}
                <Card className='m-2 w-full sm:w-1/2'>
                    <CardHeader className='w-full flex justify-center font-bold'>
                        Thông tin xe bus
                    </CardHeader>
                    <CardBody>
                        <div className='flex gap-8 items-center my-2'>
                            <span>Xe bus hiện tại: <Snippet symbol="" color="default">{manipulatePickupPointData?.result?.bus.numberPlate}</Snippet></span>
                            <Select
                                label='Trạng thái'
                                placeholder='Chọn trạng thái'
                                value={manipulatePickupPointData?.result?.bus.status}
                                defaultSelectedKeys={[bus_status_map.find((item) => item.value === manipulatePickupPointData?.result?.bus.status)?.value || '']}
                                onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                                    setStatus(event.target.value);
                                }}
                                className='w-1/2'
                                color={validateColor(bus_status_map.find((item) => item.value == status)?.color || 'default')}
                            >
                                {
                                    bus_status_map.map((item, index) => {
                                        return (
                                            <SelectItem key={item.value} value={item.value} color={validateColor(item.color)}>
                                                {item.label}
                                            </SelectItem>
                                        )
                                    })
                                }
                            </Select>
                        </div>

                        <div className='flex gap-8 items-center my-2'>
                            <span className='flex items-center'>Tài xế:
                                <User
                                    name={manipulatePickupPointData?.result?.driver.name}
                                >
                                    {manipulatePickupPointData?.result?.driver.name}
                                </User>
                            </span>
                            <span className='flex items-center'>
                                Phụ xe:
                                <User
                                    name={manipulatePickupPointData?.result?.driverMate.name}
                                >
                                    {manipulatePickupPointData?.result?.driverMate.name}
                                </User>
                            </span>
                        </div>

                        <Button color='primary' className='w-1/12'
                            onClick={() => {
                                onOpenUpdateBusModal();
                            }}
                        >
                            Lưu
                        </Button>
                    </CardBody>
                </Card>

                {/* card ride info */}
                <Card className='m-2  w-full sm:w-1/2 justify-center'>
                    <CardHeader className='w-full flex justify-center font-bold'>
                        Thông tin chuyến đi
                    </CardHeader>
                    <CardBody>
                        <div className='flex items-center my-2 gap-8'>
                            <span>Chuyến hiện tại: <Snippet symbol="" color="default">{manipulatePickupPointData?.result?.ride.id}</Snippet> </span>
                            <Select
                                label='Trạng thái'
                                placeholder='Chọn trạng thái'
                                value={manipulatePickupPointData?.result?.ride.status}
                                defaultSelectedKeys={[manipulatePickupPointData?.result?.ride.status || '']}
                                onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                                    setStatusRide(event.target.value);
                                }}
                                className='w-1/2'
                                color={validateColor(ride_status_map.find((item) => item.value == statusRide)?.color || 'default')}
                            >
                                {
                                    ride_status_map.map((item, index) => {
                                        return (
                                            <SelectItem key={item.value} value={item.value} color={validateColor(item.color)}>
                                                {item.label}
                                            </SelectItem>
                                        )
                                    })
                                }
                            </Select>
                        </div>

                        <div className='mt-4 flex gap-8'>
                            <span>Thời gian bắt đầu: {convertStringInstantToDateTime(manipulatePickupPointData?.result?.ride.startAt)} </span>
                            <span>Chiều đi: {manipulatePickupPointData?.result?.ride.isToSchool ? 'Đến trường' : 'Về nhà'}</span>
                        </div>

                        <Button color='primary' className='w-1/12 mt-4'
                            onClick={() => {
                                onOpenUpdateRideModal();
                            }}
                        >
                            Lưu
                        </Button>
                    </CardBody>
                </Card>
            </div>

            <div className='flex justify-between w-auto'>
                <div className='w-1/2 mr-4'>
                    <Autocomplete
                        placeholder="Nhập địa điểm"
                        allowsCustomValue
                        startContent={<SearchIcon />}
                        items={autoCompleteData?.features.map((item) => item.properties) || []}
                        className="m-2 w-full"
                        selectorIcon={false}
                        onInputChange={(value) => {
                            debounceSetAutoCompleteQuery(value);
                        }}
                        onSelectionChange={
                            (selectedId) => {
                                const selectedItem = autoCompleteData?.features.find((item) => item.properties.id === selectedId);
                                if (selectedItem) {
                                    setSelectedAutoCompleteData(selectedItem);
                                }
                            }
                        }
                        isClearable={false}
                    >
                        {(item) =>
                            <AutocompleteItem
                                key={item.id}
                                textValue={`${item.name ?? ''}, ${item.county ?? ''}, ${item.region ?? ''}, ${item.country ?? ''}`}
                            >
                                <div className="flex gap-2 items-center">
                                    <div className='flex-shrink-0'>
                                        <LocationIcon />
                                    </div>

                                    <div className='flex flex-col'>
                                        {`${item.name ?? ''}, ${item.county ?? ''}, ${item.region ?? ''}, ${item.country ?? ''}`}
                                    </div>
                                </div>

                            </AutocompleteItem>
                        }
                    </Autocomplete>

                    <div className='flex justify-between'>
                        <div></div>
                        <Button
                            color={enableClickMap ? 'primary' : 'default'}
                            onClick={() => setEnableClickMap(!enableClickMap)}
                        >
                            Chấm điểm
                        </Button>
                    </div>

                    {/* Accordion pickupPoints & Student inside */}
                    <div className='my-2 ml-2'>
                        <Accordion variant='shadow'>
                            {
                                (manipulatePickupPointData?.result?.pickupPointWithStudents || []).map((item, index) => {
                                    return (
                                        <AccordionItem
                                            key={index}
                                            title={
                                                <div className='flex justify-between items-center'>
                                                    <div className='flex flex-col'>
                                                        {item.pickupPoint.address}
                                                    </div>
                                                    {/* select status here */}
                                                    <div className='flex gap-2 w-1/3 items-center'>
                                                        <Select
                                                            label='Trạng thái'
                                                            placeholder='Chọn trạng thái'
                                                            value={item.ridePickupPoint.status}
                                                            defaultSelectedKeys={[ride_pickup_point_status_map.find((mapItem) => mapItem.value === item.ridePickupPoint.status)?.value || '']}
                                                            onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                                                                setStatusRidePickupPoint(event.target.value);
                                                                setSelectedPickupPointId(item.pickupPoint.id);
                                                            }}
                                                            color={validateColor(ride_pickup_point_status_map.find((mapItem) => mapItem.value == item.ridePickupPoint.status)?.color || 'default')}
                                                        >
                                                            {
                                                                ride_pickup_point_status_map.map((mapItem, index) => {
                                                                    return (
                                                                        <SelectItem key={mapItem.value} value={mapItem.value} color={validateColor(mapItem.color)}>
                                                                            {mapItem.label}
                                                                        </SelectItem>
                                                                    )
                                                                })
                                                            }
                                                        </Select>
                                                        <Button
                                                            color='primary'
                                                            className='w-1/12'
                                                            onClick={() => {
                                                                onOpenUpdateRidePickupPointModal();
                                                            }}
                                                        >
                                                            Lưu
                                                        </Button>
                                                    </div>

                                                </div>
                                            }
                                        >
                                            <Table
                                                aria-label='Student pickup point'
                                                selectionMode='multiple'
                                                onSelectionChange={(selectedKeys) => {
                                                    if (selectedKeys === 'all') {
                                                        setStudentIds(item.studentWithPickupPoints.map((student) => student.student.id));
                                                    } else {
                                                        const keysArray = Array.from(selectedKeys).map(Number);
                                                        setStudentIds(keysArray);
                                                    }
                                                }}
                                            >
                                                <TableHeader>
                                                    <TableColumn>Tên học sinh</TableColumn>
                                                    <TableColumn>Số điện thoại</TableColumn>
                                                    <TableColumn>Trạng thái</TableColumn>
                                                    <TableColumn>Chỉ định</TableColumn>
                                                </TableHeader>
                                                <TableBody>
                                                    {item.studentWithPickupPoints.map((student, index) => {
                                                        return (
                                                            <TableRow key={student.student.id}>
                                                                <TableCell>
                                                                    <User
                                                                        name={student.student.name}
                                                                    >
                                                                        {student.student.name}
                                                                    </User>
                                                                </TableCell>
                                                                <TableCell>
                                                                    <Snippet symbol="" color="default">{student.student.phoneNumber}</Snippet>
                                                                </TableCell>
                                                                <TableCell>
                                                                    <Chip
                                                                        color={
                                                                            student.studentPickupPoint.status === 'PICKED' ? 'success' :
                                                                                student.studentPickupPoint.status === 'PICKING' ? 'primary' :
                                                                                    student.studentPickupPoint.status === 'MISSED' ? 'danger' : 'default'
                                                                        }
                                                                    >
                                                                        {student.studentPickupPoint.status}
                                                                    </Chip>
                                                                </TableCell>
                                                                <TableCell>
                                                                    {/* {student.student.numberPlateAssign == manipulatePickupPointData?.result?.bus.numberPlate ? 'Có' : 'Không'} */}
                                                                    <Chip
                                                                        color={student.student.numberPlateAssign == manipulatePickupPointData?.result?.bus.numberPlate ? 'success' : 'default'}
                                                                    >
                                                                        {student.student.numberPlateAssign == manipulatePickupPointData?.result?.bus.numberPlate ? 'Có' : 'Không'}
                                                                    </Chip>
                                                                </TableCell>
                                                            </TableRow>
                                                        )
                                                    })}
                                                </TableBody>
                                            </Table>

                                            <div className='flex justify-between m-2'>
                                                <Select
                                                    label='Chọn trạng thái'
                                                    placeholder='Chọn trạng thái'
                                                    value={statusStudentPickupPoint}
                                                    defaultSelectedKeys={[student_pickup_point_status_map.find((item) => item.value === statusStudentPickupPoint)?.value || '']}
                                                    onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                                                        setStatusStudentPickupPoint(event.target.value);
                                                    }}
                                                    className='w-1/2'
                                                    color={validateColor(student_pickup_point_status_map.find((item) => item.value == statusStudentPickupPoint)?.color || 'default')}
                                                >
                                                    {
                                                        student_pickup_point_status_map
                                                            .filter((item) => {
                                                                if (manipulatePickupPointData?.result?.ride.isToSchool && item.value == 'AT_HOME') {
                                                                    return false;
                                                                }
                                                                if (!manipulatePickupPointData?.result?.ride.isToSchool && item.value == 'AT_SCHOOL') {
                                                                    return false;
                                                                }
                                                                return item.value !== 'PICKING';
                                                            })
                                                            .map((item, index) => {
                                                                return (
                                                                    <SelectItem key={item.value} value={item.value}
                                                                        color={validateColor(item.color)}
                                                                    >
                                                                        {item.label}
                                                                    </SelectItem>
                                                                )
                                                            })
                                                    }
                                                </Select>
                                                <Button
                                                    color='primary'
                                                    className='w-1/12 mt-4'
                                                    onClick={() => {
                                                        setPickupPointId(item.pickupPoint.id);
                                                        onOpenUpdateStudentPickupPointModal();
                                                    }}
                                                >
                                                    Lưu
                                                </Button>
                                            </div>
                                        </AccordionItem>
                                    )
                                })
                            }
                        </Accordion>
                    </div>

                </div>

                {/* map */}
                <div
                    className="w-1/2 z-50"
                >
                    <Map
                        features={selectedAutoCompleteData ? [selectedAutoCompleteData] : []}
                        directionsGetResponse={directionsGetResponse}
                        enableClickMap={enableClickMap}
                        manipulatePickupPointsOutput={manipulatePickupPointData?.result}
                    />
                </div >
            </div>


            {/* modal update bus */}
            <div className='relative z-10'>
                <Modal isOpen={isUpdateBusModalOpen} onOpenChange={onOpenUpdateBusModalChange}
                >
                    <ModalContent>
                        {(onOpenAddRideConfirm) => (
                            <>
                                <ModalHeader className="flex flex-col gap-1">Xác nhận lưu trạng thái xe</ModalHeader>
                                <ModalBody>
                                    <p>
                                        Bạn có muốn lưu trạng thái xe không?
                                    </p>
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="danger" variant="light"
                                        onPress={onOpenUpdateBusModalChange}
                                    >
                                        Huỷ
                                    </Button>
                                    <Button color="primary" onPress={() => {
                                        handleUpdateEmployeeBus();
                                    }}>
                                        Xác nhận
                                    </Button>
                                </ModalFooter>
                            </>
                        )}
                    </ModalContent>
                </Modal>
            </div>

            {/* modal update ride */}
            <div className='relative z-10'>
                <Modal isOpen={isUpdateRideModalOpen} onOpenChange={onOpenUpdateRideModalChange}
                >
                    <ModalContent>
                        {(onOpenAddRideConfirm) => (
                            <>
                                <ModalHeader className="flex flex-col gap-1">Xác nhận lưu chuyến</ModalHeader>
                                <ModalBody>
                                    <p>
                                        Bạn có muốn lưu chuyến không?
                                    </p>
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="danger" variant="light"
                                        onPress={onOpenUpdateRideModalChange}
                                    >
                                        Huỷ
                                    </Button>
                                    <Button color="primary" onPress={() => {
                                        handleUpdateEmployeeRide();
                                    }}>
                                        Xác nhận
                                    </Button>
                                </ModalFooter>
                            </>
                        )}
                    </ModalContent>
                </Modal>
            </div>

            {/* modal update ride pickup point */}
            <div className='relative z-10'>
                <Modal isOpen={isUpdateRidePickupPointModalOpen} onOpenChange={onOpenUpdateRidePickupPointModalChange}
                >
                    <ModalContent>
                        {(onOpenAddRideConfirm) => (
                            <>
                                <ModalHeader className="flex flex-col gap-1">Xác nhận lưu trạng thái điểm đón</ModalHeader>
                                <ModalBody>
                                    <p>
                                        Bạn có muốn lưu trạng thái điểm đón không?
                                    </p>
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="danger" variant="light"
                                        onPress={onOpenUpdateRidePickupPointModalChange}
                                    >
                                        Huỷ
                                    </Button>
                                    <Button color="primary" onPress={() => {
                                        handleUpdateEmployeeRidePickupPoint();
                                    }}>
                                        Xác nhận
                                    </Button>
                                </ModalFooter>
                            </>
                        )}
                    </ModalContent>
                </Modal>
            </div>

            {/* modal update student pickup point */}
            <div className='relative z-10'>
                <Modal isOpen={isUpdateStudentPickupPointModalOpen} onOpenChange={onOpenUpdateStudentPickupPointModalChange}
                >
                    <ModalContent>
                        {(onOpenAddRideConfirm) => (
                            <>
                                <ModalHeader className="flex flex-col gap-1">Xác nhận lưu trạng thái học sinh</ModalHeader>
                                <ModalBody>
                                    <p>
                                        Bạn có muốn lưu trạng thái học sinh không?
                                    </p>
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="danger" variant="light"
                                        onPress={onOpenUpdateStudentPickupPointModalChange}
                                    >
                                        Huỷ
                                    </Button>
                                    <Button color="primary" onPress={() => {
                                        handleUpdateEmployeeStudentPickupPoint();
                                    }}>
                                        Xác nhận
                                    </Button>
                                </ModalFooter>
                            </>
                        )}
                    </ModalContent>
                </Modal>

            </div>
        </div>
    );
};

export default EmployeeMonitoring;